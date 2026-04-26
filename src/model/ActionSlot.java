package model;
/**
 * Represents non-land action slots (Chance, Community Chest, Tax, Jail, etc.).
 * In this simplified version, these slots have no special effect.
 */
public class ActionSlot extends Slot {

    /**
     *
     * @param slotNumber
     * @param name
     */
    public ActionSlot(int slotNumber, String name) {
        super(slotNumber, name);
    }

    /**
     *
     * @param p
     */
    @Override
    public void onLand(Player p) {
        // No action for now.
    }
}
