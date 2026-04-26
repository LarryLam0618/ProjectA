package model;
/**
 * Represents one player in the game.
 * Tracks player ID, name, balance, board position, and active status.
 * Provides methods for movement, buying land, paying rent, bankruptcy,
 * and trading land.
 */
public class Player {

    /**
     *
     */
    public static final int BOARD_SIZE = 40;

    private int id;
    private String name;
    private int balance;
    private int position;
    private int round;
    private boolean isActive;

    /**
     *
     * @param id
     * @param name
     * @param initialBalance
     */
    public Player(int id, String name, int initialBalance) {
        this.id = id;
        this.name = name;
        this.balance = initialBalance;
        this.position = 0;
        this.round = 0;
        this.isActive = true;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public int getBalance() {
        return balance;
    }

    /**
     *
     * @param balance
     */
    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     *
     * @return
     */
    public int getPosition() {
        return position;
    }

    /**
     *
     * @param position
     */
    public void setPosition(int position) {
        this.position = ((position % BOARD_SIZE) + BOARD_SIZE) % BOARD_SIZE;
    }

    /**
     *
     * @return
     */
    public int getRound() {
        return round;
    }

    /**
     *
     * @param round
     */
    public void setRound(int round) {
        this.round = Math.max(0, round);
    }

    /**
     *
     */
    public void advanceRound() {
        round++;
    }

    /**
     *
     * @return
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     *
     * @param active
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     *
     * @param steps
     */
    public void move(int steps) {
        setPosition(position + steps);
    }

    /**
     *
     * @param slot
     */
    public void buyLand(LandSlot slot) {
        if (slot == null || slot.getOwner() != null || balance < slot.getPrice()) {
            return;
        }
        balance -= slot.getPrice();
        slot.setOwner(this);
    }

    /**
     *
     * @param amount
     */
    public void payRent(int amount) {
        if (balance < amount) {
            declareBankruptcy();
            return;
        }
        balance -= amount;
    }

    /**
     *
     */
    public void declareBankruptcy() {
        isActive = false;
        balance = 0;
    }

    /**
     *
     * @param slot
     * @param price
     * @param buyer
     */
    public void tradeLand(LandSlot slot, int price, Player buyer) {
        if (slot == null || buyer == null || slot.getOwner() != this) {
            return;
        }
        slot.transferOwnership(buyer, price);
    }
}
