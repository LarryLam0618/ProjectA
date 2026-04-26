package model;
/**
 * Central class that manages the entire Monopoly game state.
 * Holds the Board, all Players, the current turn index, and controls 
 * game flow, winning conditions, and GO bonus awarding.
 */
public class Game {
    private Player[] players;
    private Board board;
    private int currentTurnIndex;
    private boolean isRunning;

    /**
     *
     * @param players
     * @param board
     */
    public Game(Player[] players, Board board) {
        this.players = players;
        this.board = board;
        this.currentTurnIndex = 0;
        this.isRunning = false;
    }

    /**
     *
     */
    public void startGame() {
        isRunning = true;
        currentTurnIndex = 0;
        for (Player player : players) {
            player.setActive(true);
            player.setPosition(0);
            player.setRound(0);
        }
    }

    /**
     *
     */
    public void nextTurn() {
        if (!isRunning) {
            return;
        }

        int nextIndex = currentTurnIndex;
        for (int checked = 0; checked < players.length; checked++) {
            nextIndex = (nextIndex + 1) % players.length;
            if (players[nextIndex].isActive()) {
                currentTurnIndex = nextIndex;
                return;
            }
        }

        isRunning = false;
    }

    /**
     *
     * @return
     */
    public Player checkWinner() {
        Player winner = null;
        int activeCount = 0;
        for (Player player : players) {
            if (player.isActive()) {
                winner = player;
                activeCount++;
            }
        }
        if (activeCount <= 1) {
            isRunning = false;
            return winner;
        }
        return null;
    }

    /**
     *
     * @param p
     */
    public void awardPassGoBonus(Player p) {
        p.setBalance(p.getBalance() + 2000);
    }

    /**
     *
     * @return
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     *
     * @return
     */
    public Board getBoard() {
        return board;
    }

    /**
     *
     * @return
     */
    public Player getCurrentPlayer() {
        return players[currentTurnIndex];
    }

    /**
     *
     * @return
     */
    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    /**
     *
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     *
     * @param currentTurnIndex
     */
    public void setCurrentTurnIndex(int currentTurnIndex) {
        if (currentTurnIndex >= 0 && currentTurnIndex < players.length) {
            this.currentTurnIndex = currentTurnIndex;
        }
    }
}
