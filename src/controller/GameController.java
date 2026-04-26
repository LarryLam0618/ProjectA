package controller;

import model.Dice;
import model.Game;
import model.GoSlot;
import model.LandSlot;
import model.Player;
import model.Slot;
import view.GameView;

import java.util.ArrayList;
import java.util.List;
/**
 * Main controller responsible for all core gameplay logic.
 * Handles dice rolling, land buying, rent payment, trading,
 * bankruptcy processing, turn management, and UI updates.
 */
public class GameController {
    private final Game game;
    private final GameView view;
    private final Dice dice;
    private boolean hasRolledThisTurn;
    private boolean canBuyThisTurn;

    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
        this.dice = new Dice();
        this.hasRolledThisTurn = false;
        this.canBuyThisTurn = false;
        refreshActionButtons();
    }

    public void handleRoll() {
        if (!game.isRunning()) {
            return;
        }

        if (hasRolledThisTurn) {
            view.showMessage("You already rolled. Buy land or click End Turn.");
            refreshActionButtons();
            return;
        }

        Player current = game.getCurrentPlayer();
        if (!current.isActive()) {
            game.nextTurn();
            resetTurnState();
            refreshActionButtons();
            return;
        }

        int roll = dice.roll();
        int oldPos = current.getPosition();
        current.move(roll);
        boolean passedGo = current.getPosition() < oldPos;

        if (passedGo) {
            current.advanceRound();
            game.awardPassGoBonus(current);
            view.showGoBonusDialog(current, 2000);
            view.showMessage(current.getName() + " passed GO, reached round " + current.getRound() + ", and received 2000.");
        }

        Slot slot = game.getBoard().getSlot(current.getPosition());
        view.showMessage(current.getName() + " rolled " + roll + " and landed on " + slot.getName() + ".");
        view.update();

        if (slot instanceof LandSlot) {
            LandSlot land = (LandSlot) slot;
            applyLandEffect(current, land);
        } else if (!(passedGo && slot instanceof GoSlot)) {
            slot.onLand(current);
        }

        if (!game.isRunning()) {
            refreshActionButtons();
            view.update();
            return;
        }

        Player winner = game.checkWinner();
        if (winner != null) {
            view.showMessage("Winner: " + winner.getName());
            view.showWinnerScreen(winner);
            refreshActionButtons();
            view.update();
            return;
        }

        if (!current.isActive()) {
            game.nextTurn();
            resetTurnState();
            if (game.isRunning()) {
                view.showMessage("Turn ended due to bankruptcy. Next player: " + game.getCurrentPlayer().getName());
            }
            refreshActionButtons();
            view.update();
            return;
        }

        hasRolledThisTurn = true;
        canBuyThisTurn = isBuyableSlot(current);
        if (canBuyThisTurn) {
            view.showMessage("You may buy this land, or click End Turn.");
        } else {
            view.showMessage("No buy available. Click End Turn to continue.");
        }

        refreshActionButtons();
        view.update();
    }

    public void handleBuy() {
        if (!hasRolledThisTurn) {
            view.showMessage("Roll first.");
            refreshActionButtons();
            return;
        }
        if (!canBuyThisTurn) {
            view.showMessage("You cannot buy now. Click End Turn.");
            refreshActionButtons();
            return;
        }

        Player current = game.getCurrentPlayer();
        Slot slot = game.getBoard().getSlot(current.getPosition());

        if (slot instanceof LandSlot) {
            LandSlot land = (LandSlot) slot;
            int before = current.getBalance();
            land.buy(current);
            if (before != current.getBalance()) {
                view.showMessage(current.getName() + " bought " + land.getName() + ".");
                canBuyThisTurn = false;
            } else {
                view.showMessage("Cannot buy " + land.getName() + ".");
            }
        } else {
            view.showMessage("This slot is not purchasable.");
        }
        refreshActionButtons();
        view.update();
    }

    public void setNextRoll(int value) {
        dice.setNextRoll(value);
    }

    public Integer getNextRoll() {
        return dice.getNextRoll();
    }

    public void handleTradeRequest() {
        Player current = game.getCurrentPlayer();
        List<Player> tradePartners = getTradePartners(current);
        List<LandSlot> buyable = game.getBoard().getTradableLandsFor(current);
        List<LandSlot> sellable = game.getBoard().getOwnedLandsFor(current);
        GameView.TradeRequest request = view.showTradeDialog(current, tradePartners, buyable, sellable);
        if (request != null) {
            handleTrade(request);
        }
    }

    public void handleTrade(GameView.TradeRequest request) {
        if (request == null || request.getLand() == null || request.getSeller() == null || request.getBuyer() == null) {
            view.showMessage("Invalid trade request.");
            return;
        }

        LandSlot slot = request.getLand();
        Player seller = request.getSeller();
        Player buyer = request.getBuyer();
        int price = request.getPrice();

        if (slot.getOwner() != seller) {
            view.showMessage("Trade failed: property owner changed.");
            view.update();
            return;
        }
        if (!seller.isActive()) {
            view.showMessage("Trade failed: seller is bankrupt.");
            view.update();
            return;
        }
        if (!buyer.isActive()) {
            view.showMessage("Trade failed: buyer is bankrupt.");
            view.update();
            return;
        }
        if (buyer.getBalance() < price) {
            view.showMessage("Trade rejected: " + buyer.getName() + " does not have enough money.");
            view.showInsufficientFundsDialog(buyer, price);
            view.update();
            return;
        }

        boolean isAgreed = view.confirmTradeRequest(seller, buyer, slot, price);

        if (!isAgreed) {
            view.showMessage("Transaction cancelled: " + seller.getName() + " refused " + buyer.getName() + "'s offer.");
            view.update();
            return; 
        }

        seller.tradeLand(slot, price, buyer);
        if (slot.getOwner() == buyer) {
            if (request.isSelling()) {
                view.showMessage("Trade successful: " + seller.getName() + " sold " + slot.getName()
                        + " to " + buyer.getName() + ".");
            } else {
                view.showMessage("Trade successful: " + buyer.getName() + " acquired " + slot.getName()
                        + " from " + seller.getName() + ".");
            }
        } else {
            view.showMessage("Trade failed.");
        }
        view.update();
    }

    public void handleBankruptcy(Player p) {
        if (p != null) {
            view.showMessage(p.getName() + " declared bankruptcy.");
            applyBankruptcy(p, "Marked bankrupt by game action.");
            announceWinnerIfAny();
            refreshActionButtons();
            view.update();
        }
    }

    public void handleEndTurn() {
        if (!hasRolledThisTurn) {
            view.showMessage("Roll first before ending your turn.");
            refreshActionButtons();
            return;
        }

        game.nextTurn();
        resetTurnState();
        view.showMessage("Turn ended. Next player: " + game.getCurrentPlayer().getName());
        refreshActionButtons();
        view.update();
    }

    private void applyLandEffect(Player current, LandSlot land) {
        Player owner = land.getOwner();
        if (owner == null || owner == current) {
            return;
        }

        int baseRent = land.calculateRent();
        int multiplier = game.getBoard().getSideRentMultiplier(land, owner);
        int totalRent = baseRent * multiplier;

        if (current.getBalance() < totalRent) {
            view.showMessage(current.getName() + " cannot pay rent and is bankrupt. All lands reset to No Owner.");
            String reason = String.format("Unable to pay $%d rent to %s for %s.", totalRent, owner.getName(), land.getName());
            applyBankruptcy(current, reason);
            announceWinnerIfAny();
            return;
        }

        current.payRent(totalRent);
        owner.setBalance(owner.getBalance() + totalRent);
        view.showRentPaidDialog(current, owner, land, totalRent);

        if (multiplier > 1) {
            view.showMessage("Rent multiplier x" + multiplier + " for consecutive ownership on this side.");
        }
    }

    private boolean isBuyableSlot(Player current) {
        Slot slot = game.getBoard().getSlot(current.getPosition());
        if (!(slot instanceof LandSlot)) {
            return false;
        }
        LandSlot land = (LandSlot) slot;
        return land.getOwner() == null && current.getBalance() >= land.getPrice();
    }

    private void resetTurnState() {
        hasRolledThisTurn = false;
        canBuyThisTurn = false;
    }

    private void refreshActionButtons() {
        boolean canRoll = !hasRolledThisTurn && game.getCurrentPlayer().isActive();
        boolean canBuy = hasRolledThisTurn && canBuyThisTurn;
        boolean canEndTurn = hasRolledThisTurn;
        boolean canTrade = game.getCurrentPlayer().isActive() && canTrade();
        view.setActionButtonState(canRoll, canBuy, canEndTurn, canTrade);
    }

    private boolean canTrade() {
        Player current = game.getCurrentPlayer();
        boolean canBuyFromOthers = !game.getBoard().getTradableLandsFor(current).isEmpty();
        boolean canSellToOthers = !game.getBoard().getOwnedLandsFor(current).isEmpty()
                && !getTradePartners(current).isEmpty();
        return canBuyFromOthers || canSellToOthers;
    }

    private List<Player> getTradePartners(Player current) {
        List<Player> partners = new ArrayList<>();
        if (current == null) {
            return partners;
        }

        for (Player player : game.getPlayers()) {
            if (player != current && player.isActive()) {
                partners.add(player);
            }
        }
        return partners;
    }

    private void announceWinnerIfAny() {
        Player winner = game.checkWinner();
        if (winner != null) {
            view.showMessage("Winner: " + winner.getName());
            view.showWinnerScreen(winner);
        }
    }

    private void applyBankruptcy(Player player, String reason) {
        view.showBankruptcyDialog(player, reason);
        player.declareBankruptcy();
        game.getBoard().releaseLandsOwnedBy(player);
        refreshActionButtons();
        view.update();
    }
}
