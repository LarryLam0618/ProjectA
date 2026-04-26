package model;

import java.util.Random;
/**
 * Handles dice rolling with values from 1 to 10.
 * Supports deterministic testing by allowing a predetermined 
 * next roll value to be set.
 */
public class Dice {
    private final Random random = new Random();
    private Integer nextRoll;

    /**
     *
     * @return
     */
    public int roll() {
        if (nextRoll != null) {
            int value = nextRoll;
            nextRoll = null;
            return value;
        }
        return random.nextInt(10) + 1;
    }

    /**
     *
     * @param value
     */
    public void setNextRoll(int value) {
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException("Next roll must be between 1 and 10.");
        }
        nextRoll = value;
    }

    /**
     *
     * @return
     */
    public Integer getNextRoll() {
        return nextRoll;
    }
}
