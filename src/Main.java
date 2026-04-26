import controller.GameController;
import controller.GameEditorController;
import model.Board;
import model.Game;
import model.Player;
import view.GameEditorDialog;
import view.GameView;

import java.io.InputStream;

/**
 *
 * @author rnpk
 */
public class Main {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Player[] players = {
            new Player(1, "Player 1", 2000),
            new Player(2, "Player 2", 2000),
            new Player(3, "Player 3", 2000),
            new Player(4, "Player 4", 2000)
        };

        Board board = new Board();
        try (InputStream inputStream = Main.class.getResourceAsStream("/resources/board.csv")) {
            board.loadFromStream(inputStream);
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot load board.csv from classpath.", ex);
        }
        Game game = new Game(players, board);

        GameView gameView = new GameView();
        GameController gameController = new GameController(game, gameView);
        GameEditorController editorController = new GameEditorController(game, gameController);
        GameEditorDialog editorDialog = new GameEditorDialog(
                gameView.getFrame(),
                editorController,
                () -> {
                    gameView.showPlayerInfo(game);
                    gameView.displayBoard(game);
                }
        );

        game.startGame();
        gameView.showMessage("Monopoly game started.");
        gameView.displayBoard(game);
        gameView.showPlayerInfo(game);

        gameView.setActionHandlers(
                () -> {
                    gameController.handleRoll();
                    gameView.showPlayerInfo(game);
                    gameView.displayBoard(game);
                },
                () -> {
                    gameController.handleBuy();
                    gameView.showPlayerInfo(game);
                    gameView.displayBoard(game);
                },
                () -> {
                    gameController.handleEndTurn();
                    gameView.showPlayerInfo(game);
                    gameView.displayBoard(game);
                },
                () -> {
                    gameController.handleTradeRequest();
                    gameView.showPlayerInfo(game);
                    gameView.displayBoard(game);
                }
        );

        gameView.setEditorShortcut(
                () -> {
                    editorDialog.setVisible(true);
                    gameView.showMessage("Editor opened. (Ctrl+Shift+E)");
                },
                () -> {
                    editorDialog.setVisible(false);
                    gameView.showMessage("Editor closed. (Esc)");
                }
        );
    }

}
