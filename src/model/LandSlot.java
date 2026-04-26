package model;
/**
 * Represents a purchasable land property on the board.
 * Extends Slot and includes price, group, and owner.
 * Handles rent calculation
 * and ownership transfer.
 */
public class LandSlot extends Slot {
    private int price;
    private String group;
    private Player owner;

    /**
     *
     * @param slotNumber
     * @param name
     * @param price
     */
    public LandSlot(int slotNumber, String name, int price) {
        this(slotNumber, name, price, "none");
    }

    /**
     *
     * @param slotNumber
     * @param name
     * @param price
     * @param group
     */
    public LandSlot(int slotNumber, String name, int price, String group) {
        super(slotNumber, name);
        this.price = price;
        this.group = group;
    }

    /**
     *
     * @return
     */
    public int getPrice() {
        return price;
    }

    /**
     *
     * @return
     */
    public Player getOwner() {
        return owner;
    }

    /**
     *
     * @return
     */
    public String getGroup() {
        return group;
    }

    /**
     *
     * @param owner
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     *
     * @param p
     */
    public void buy(Player p) {
        if (owner == null && p.getBalance() >= price) {
            p.buyLand(this);
        }
    }

    /**
     *
     * @return
     */
    public int calculateRent() {
        return Math.max(10, price / 10);
    }

    /**
     *
     * @param buyer
     * @param price
     */
    public void transferOwnership(Player buyer, int price) {
        if (owner != null && buyer != null && buyer.getBalance() >= price) {
            owner.setBalance(owner.getBalance() + price);
            buyer.setBalance(buyer.getBalance() - price);
            owner = buyer;
        }
    }

    /**
     *
     * @param p
     */
    @Override
    public void onLand(Player p) {
        // Rent is handled in GameController so board side/multiplier rules can be applied.
    }
}
