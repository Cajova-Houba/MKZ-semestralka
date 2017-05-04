package mkz.mkz_semestralka.controller;

import android.content.Intent;

import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.error.ErrorCode;
import mkz.mkz_semestralka.core.error.ErrorMessages;
import mkz.mkz_semestralka.core.game.Game;
import mkz.mkz_semestralka.core.network.LoginData;
import mkz.mkz_semestralka.core.network.daemon.DaemonActionNames;
import mkz.mkz_semestralka.ui.EndGameActivity;
import mkz.mkz_semestralka.ui.GameActivity;
import mkz.mkz_semestralka.ui.LoginActivity;

/**
 * Controller for app.
 *
 * Created on 27.04.2017.
 *
 * @author  Zdenek Vales
 */

public class Controller {

    private final static Logger logger = Logger.getLogger(Controller.class);

    public static Controller getInstance() {
        return instance;
    }

    private static Controller instance = new Controller();


    private LoginActivity loginActivity;
    private GameActivity gameActivity;
    private EndGameActivity endGameActivity;

    /**
     * Player name when waiting for opponent.
     */
    private String tmpPlayerName;


    private Controller() {
    }


    public void setLoginActivity(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    public void setEndGameActivity(EndGameActivity endGameActivity) {
        this.endGameActivity = endGameActivity;
    }

    /**
     * Handles login response from daemon. If everything is OK, changes the view and initializes new game.
     *
     * @param intent Message from daemon.
     * @param loginData Login data sent to server.
     */
    public void handleLogin(Intent intent, LoginData loginData) {
        logger.d("Starting new game.");

        String content = intent.getStringExtra(DaemonActionNames.CONTENT);
        if(content.equalsIgnoreCase(DaemonActionNames.CONTENT_OK)) {
            // login ok
            Game.getInstance().waitingForOpponent(loginData.getNick());
            tmpPlayerName = loginData.getNick();
            loginActivity.displayMainView();
        } else {
            // login not ok - display error
            ErrorCode errCode = (ErrorCode) intent.getSerializableExtra(DaemonActionNames.ERR_CODE);
            loginActivity.enableConnectBtn(true);
            loginActivity.displayErrorMessage(ErrorMessages.getErrorForCode(errCode));
        }
    }

    /**
     * Handles start new game response from daemon. If everything is OK, starts the game.
     *
     * @param intent Message from daemon.
     */
    public void handleStartNewGame(Intent intent) {
        logger.d("Handling new game response.");

        ErrorCode errorCode = (ErrorCode) intent.getSerializableExtra(DaemonActionNames.ERR_CODE);
        if(errorCode == ErrorCode.NO_ERROR) {
            // start game
            String[] players = intent.getStringExtra(DaemonActionNames.CONTENT).split(";");
            Game.getInstance().startGame(players[0],players[1]);
            gameActivity.startGame();
            gameActivity.updateStones(
                    Game.getInstance().getFirstPlayer().getStones(),
                    Game.getInstance().getSecondPlayer().getStones());

            // todo: whose turn is now?
        } else {
            // handle error
        }
    }

    /**
     * Throws sticks and returns the value.
     */
    public int throwSticks() {
        int thrown;
        if(Game.getInstance().alreadyThrown() && !Game.getInstance().canThrowAgain()) {
            thrown = Game.getInstance().throwSticks();
        } else {
            thrown = Game.getInstance().throwSticks();
            logger.d("Thrown: "+thrown+"\n");
            gameActivity.setThrowText(Integer.toString(thrown));

            if(Game.getInstance().canThrowAgain()) {
                gameActivity.displayToast("Hozeno: "+thrown+". Házej znovu.");
            } else {
                gameActivity.displayToast("Hozeno: "+thrown+".");
            }
        }

        if(Game.getInstance().canThrowAgain()) {
            gameActivity.enableThrowButton(true);
            gameActivity.enableEndTurnButton(false);
        } else {
            gameActivity.enableThrowButton(false);
            gameActivity.enableEndTurnButton(true);
        }

        return thrown;
    }

    public String getTmpPlayerName() {
        return tmpPlayerName;
    }
}
