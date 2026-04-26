package model;
/**
 * Represents the GO slot.
 * When a player passes or lands on GO, awards the $2000 bonus.
 */
public class GoSlot extends Slot {
    private int bonus;

    /**
     *
     * @param slotNumber
     * @param name
     * @param bonus
     */
    public GoSlot(int slotNumber, String name, int bonus) {
        super(slotNumber, name);
        this.bonus = bonus;
    }

    /**
     *
     * @return
     */
    public int getBonus() {
        return bonus;
    }

    /**
     *
     * @param p
     */
    @Override
    public void onLand(Player p) {
        p.setBalance(p.getBalance() + bonus);
    }
}
