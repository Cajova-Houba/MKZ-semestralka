package mkz.mkz_semestralka.controller;

import android.content.Intent;
import android.os.CountDownTimer;

import java.io.Serializable;
import java.util.Arrays;

import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.error.ErrorCode;
import mkz.mkz_semestralka.core.error.ErrorMessages;
import mkz.mkz_semestralka.core.game.Game;
import mkz.mkz_semestralka.core.message.received.StartTurnReceivedMessage;
import mkz.mkz_semestralka.core.network.LoginData;
import mkz.mkz_semestralka.core.network.daemon.DaemonActionNames;
import mkz.mkz_semestralka.core.network.daemon.DaemonService;
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

    /**
     * Max time for turn = 2 minutes.
     */
    public static final int MAX_TURN_TIME = 2*60;

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

    /**
     * Turn timer. If it expires, turn is automatically ended.
     */
    private CountDownTimer turnTimer;

    /**
     * Turn timer passed.
     */
    private boolean timerPassed;

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

            if(!Game.getInstance().isMyTurn()) {
                gameActivity.disableButtons();
            } else {
                newTurn(true,Game.getInstance().getFirstPlayer().getStones(),
                        Game.getInstance().getSecondPlayer().getStones());
            }
        } else {
            // todo: handle error
        }
    }

    /**
     * Handles response to end turn message. Either OK or error is expected.
     *
     * @param intent Message from daemon.
     */
    public void handleEndTurnResponse(Intent intent, DaemonService daemonService) {
        logger.d("Handling new game response.");

        ErrorCode errorCode = (ErrorCode) intent.getSerializableExtra(DaemonActionNames.ERR_CODE);
        if(errorCode == ErrorCode.NO_ERROR) {
            logger.d("Turn is ok.");

            // wait for new turn
            daemonService.waitForNewTurn();
        } else {
            // todo: handle error

        }
    }

    /**
     * Handles new turn message.
     *
     * @param intent
     */
    public void handleNewTurn(Intent intent) {
        logger.d("Handling new turn message.");
        ErrorCode errorCode = (ErrorCode) intent.getSerializableExtra(DaemonActionNames.ERR_CODE);
        if (errorCode == ErrorCode.NO_ERROR) {
            // check if it's new turn or end game
            Serializable content = intent.getSerializableExtra(DaemonActionNames.CONTENT);
            if(content instanceof StartTurnReceivedMessage) {
                // new turn
                StartTurnReceivedMessage msg = (StartTurnReceivedMessage) content;
                newTurn(false, msg.getFirstPlayerStones(), msg.getSecondPlayerStones());
            } else {
                // end game
                // todo: end game
            }

        }
    }

    /**
     * Handle new turn.
     * @param firstTurn
     */
    public void newTurn(boolean firstTurn, int[] firstPlayerStones, int[] secondPlayerStones) {
        if(!firstTurn) {
            Game.getInstance().newTurn(firstPlayerStones, secondPlayerStones);
        }
        gameActivity.newTurn();
        gameActivity.updateStones(Game.getInstance().getFirstPlayer().getStones(),
                Game.getInstance().getSecondPlayer().getStones());
        startTimer();
    }

    /**
     * Ends the turn and waits for START_TURN message
     */
    public void endTurn(DaemonService daemonService) {
        if(Game.getInstance().canThrowAgain() && !timerPassed) {
            logger.e("Cannot end turn if player can throw again!");
            gameActivity.displayToast("Hráč musí házet znovu!");
            return;
        }

        stopTimer();
        gameActivity.resetTimerText();
        Game.getInstance().endTurn();
        gameActivity.disableButtons();
        daemonService.endTurn(Game.getInstance().getFirstPlayer().getStones(),
                Game.getInstance().getSecondPlayer().getStones());
        // todo: wait for turn confirm
        logger.d("Ending turn with player 1 stones: "+ Arrays.toString(Game.getInstance().getFirstPlayer().getStones())+
                ", player 2 stones: "+Arrays.toString(Game.getInstance().getSecondPlayer().getStones())+".");
    }

    public void stopTimer() {
        if(turnTimer == null) {
            return;
        }
        turnTimer.cancel();
    }

    public void startTimer() {
        timerPassed = false;
        turnTimer = new CountDownTimer(MAX_TURN_TIME*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                gameActivity.updateTimerText((int) (millisUntilFinished / 1000));
            }

            public void onFinish() {
                logger.d("Time for turn expired.");
                timerPassed = true;
                endTurn(gameActivity.getClientDaemonService());
            }
        };
        turnTimer.start();
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
