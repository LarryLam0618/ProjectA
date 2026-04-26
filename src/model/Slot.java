package model;
/**
 * Abstract base class for all slots on the board.
 * Provides common properties (slotNumber, name) and the abstract onLand method.
 */
public abstract class Slot {

    /**
     *
     */
    protected int slotNumber;

    /**
     *
     */
    protected String name;

    /**
     *
     * @param slotNumber
     * @param name
     */
    protected Slot(int slotNumber, String name) {
        this.slotNumber = slotNumber;
        this.name = name;
    }

    /**
     *
     * @return
     */
    public int getSlotNumber() {
        return slotNumber;
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
     * @param p
     */
    public abstract void onLand(Player p);
}
