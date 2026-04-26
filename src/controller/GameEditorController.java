package controller;

import model.Game;
import model.LandSlot;
import model.Player;
import model.Slot;

import java.util.List;
/**
 * Controller for the hidden Game Editor.
 * Provides methods to modify game state for testing and debugging:
 * land ownership, player balance/position/status, turn order, 
 * and next dice roll.
 */
public class GameEditorController {
    private final Game game;
    private final GameController gameController;

    /**
     *
     * @param game
     * @param gameController
     */
    public GameEditorController(Game game, GameController gameController) {
        this.game = game;
        this.gameController = gameController;
    }

    /**
     *
     * @param slot
     * @param player
     */
    public void changeOwnership(Slot slot, Player player) {
        if (slot instanceof LandSlot) {
            LandSlot land = (LandSlot) slot;
            land.setOwner(player);
        }
    }

    /**
     *
     * @param player
     * @param amount
     */
    public void modifyBalance(Player player, int amount) {
        if (player != null) {
            player.setBalance(player.getBalance() + amount);
        }
    }

    /**
     *
     * @param player
     * @param pos
     */
    public void modifyPosition(Player player, int pos) {
        if (player != null) {
            player.setPosition(pos);
        }
    }

    /**
     *
     * @param player
     * @param status
     */
    public void modifyStatus(Player player, boolean status) {
        if (player != null) {
            if (!status) {
                player.declareBankruptcy();
                game.getBoard().releaseLandsOwnedBy(player);
            } else {
                player.setActive(true);
            }
        }
    }

    /**
     *
     * @param player
     */
    public void changeTurn(Player player) {
        if (player == null) {
            return;
        }
        Player[] players = game.getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                game.setCurrentTurnIndex(i);
                break;
            }
        }
    }

    /**
     *
     * @return
     */
    public Player[] getPlayers() {
        return game.getPlayers();
    }

    /**
     *
     * @return
     */
    public Slot[] getSlots() {
        return game.getBoard().getSlots();
    }

    /**
     *
     * @return
     */
    public int getCurrentTurnIndex() {
        return game.getCurrentTurnIndex();
    }

    /**
     *
     * @param playerIndex
     * @param amount
     */
    public void editorSetBalance(int playerIndex, int amount) {
        Player player = getPlayerByIndex(playerIndex);
        if (player != null) {
            player.setBalance(amount);
        }
    }

    /**
     *
     * @param playerIndex
     * @param pos
     */
    public void editorSetPosition(int playerIndex, int pos) {
        Player player = getPlayerByIndex(playerIndex);
        if (player != null) {
            player.setPosition(pos);
        }
    }

    /**
     *
     * @param playerIndex
     * @param bankrupt
     */
    public void editorSetBankrupt(int playerIndex, boolean bankrupt) {
        Player player = getPlayerByIndex(playerIndex);
        if (player != null) {
            if (bankrupt) {
                player.declareBankruptcy();
                game.getBoard().releaseLandsOwnedBy(player);
            } else {
                player.setActive(true);
            }
        }
    }

    /**
     *
     * @param slotIndex
     * @param ownerIndex
     */
    public void editorSetOwner(int slotIndex, int ownerIndex) {
        Slot[] slots = game.getBoard().getSlots();
        if (slotIndex < 0 || slotIndex >= slots.length) {
            return;
        }
        Slot slot = slots[slotIndex];
        if (!(slot instanceof LandSlot)) {
            return;
        }
        LandSlot land = (LandSlot) slot;
        Player owner = getPlayerByIndex(ownerIndex);
        land.setOwner(owner);
    }

    /**
     *
     * @param ownerIndex
     */
    public void editorSetAllLandOwners(int ownerIndex) {
        Player owner = getPlayerByIndex(ownerIndex);
        for (Slot slot : game.getBoard().getSlots()) {
            if (slot instanceof LandSlot) {
                ((LandSlot) slot).setOwner(owner);
            }
        }
    }

    /**
     *
     * @param playerIndex
     */
    public void editorSetTurn(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < game.getPlayers().length) {
            game.setCurrentTurnIndex(playerIndex);
        }
    }

    /**
     *
     * @param value
     */
    public void editorSetNextRoll(int value) {
        gameController.setNextRoll(value);
    }

    /**
     *
     * @return
     */
    public Integer getNextRoll() {
        return gameController.getNextRoll();
    }

    /**
     *
     * @param slotIndex
     * @param buyerIndex
     * @param price
     * @return
     */
    public String editorTradeLand(int slotIndex, int buyerIndex, int price) {
        if (price <= 0) {
            return "Price must be greater than 0.";
        }

        Slot[] slots = game.getBoard().getSlots();
        if (slotIndex < 0 || slotIndex >= slots.length) {
            return "Invalid slot.";
        }

        Slot slot = slots[slotIndex];
        if (!(slot instanceof LandSlot)) {
            return "Selected slot is not land.";
        }

        LandSlot land = (LandSlot) slot;
        Player seller = land.getOwner();
        if (seller == null) {
            return "This land has no owner.";
        }

        Player buyer = getPlayerByIndex(buyerIndex);
        if (buyer == null) {
            return "Invalid buyer.";
        }
        if (buyer == seller) {
            return "Buyer already owns this land.";
        }
        if (!buyer.isActive()) {
            return "Buyer is bankrupt.";
        }
        if (buyer.getBalance() < price) {
            return "Buyer does not have enough money.";
        }

        seller.tradeLand(land, price, buyer);
        if (land.getOwner() != buyer) {
            return "Trade failed.";
        }
        return null;
    }

    /**
     *
     * @param slotIndexes
     * @param buyerIndex
     * @param priceEach
     * @return
     */
    public String editorTradeLands(List<Integer> slotIndexes, int buyerIndex, int priceEach) {
        if (slotIndexes == null || slotIndexes.isEmpty()) {
            return "Select at least one land.";
        }
        if (priceEach < 0) {
            return "Price must be 0 or greater.";
        }

        Player buyer = getPlayerByIndex(buyerIndex);
        if (buyer == null) {
            return "Invalid buyer.";
        }
        if (!buyer.isActive()) {
            return "Buyer is bankrupt.";
        }

        long totalPrice = (long) priceEach * slotIndexes.size();
        if (totalPrice > Integer.MAX_VALUE) {
            return "Total trade price is too large.";
        }
        if (buyer.getBalance() < totalPrice) {
            return "Buyer does not have enough money.";
        }

        Slot[] slots = game.getBoard().getSlots();
        LandSlot[] lands = new LandSlot[slotIndexes.size()];
        Player[] sellers = new Player[slotIndexes.size()];
        for (int i = 0; i < slotIndexes.size(); i++) {
            Integer slotIndex = slotIndexes.get(i);
            if (slotIndex == null || slotIndex < 0 || slotIndex >= slots.length) {
                return "Invalid slot.";
            }

            Slot slot = slots[slotIndex];
            if (!(slot instanceof LandSlot)) {
                return "Selected slot is not land.";
            }

            LandSlot land = (LandSlot) slot;
            Player seller = land.getOwner();
            if (seller == null) {
                return land.getName() + " has no owner.";
            }
            if (seller == buyer) {
                return "Buyer already owns " + land.getName() + ".";
            }

            lands[i] = land;
            sellers[i] = seller;
        }

        for (int i = 0; i < lands.length; i++) {
            sellers[i].setBalance(sellers[i].getBalance() + priceEach);
            lands[i].setOwner(buyer);
        }
        buyer.setBalance(buyer.getBalance() - (int) totalPrice);
        return null;
    }

    private Player getPlayerByIndex(int index) {
        Player[] players = game.getPlayers();
        if (index < 0 || index >= players.length) {
            return null;
        }
        return players[index];
    }
}
