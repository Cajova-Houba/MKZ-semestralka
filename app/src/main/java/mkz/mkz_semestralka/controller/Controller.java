package mkz.mkz_semestralka.controller;

import android.content.Intent;
import android.os.CountDownTimer;

import java.io.Serializable;
import java.util.Arrays;

import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.error.ErrorCode;
import mkz.mkz_semestralka.core.error.ErrorMessages;
import mkz.mkz_semestralka.core.game.Game;
import mkz.mkz_semestralka.core.game.PlayerNum;
import mkz.mkz_semestralka.core.message.received.EndGameReceivedMessage;
import mkz.mkz_semestralka.core.message.received.StartTurnReceivedMessage;
import mkz.mkz_semestralka.core.network.LoginData;
import mkz.mkz_semestralka.core.network.daemon.DaemonActionNames;
import mkz.mkz_semestralka.core.network.daemon.DaemonService;
import mkz.mkz_semestralka.ui.EndGameActivity;
import mkz.mkz_semestralka.ui.GameActivity;
import mkz.mkz_semestralka.ui.LoginActivity;
import mkz.mkz_semestralka.ui.components.Stone;

/**
 * Controller for app.
 *
 * Created on 27.04.2017.
 *
 * @author  Zdenek Vales
 */
// todo: exit from game
// todo: handle buggy stuff when switching activities - especially when no receiver is registered for broadcasts
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

    /**
     * Flag indicating the client is logged to the server.
     */
    private boolean logged;

	private ClientDaemonService daemonService = ClientDaemonService.getInstance();
	
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
		String intentId = intent.getStringExtra(DaemonActionNames.ID);
		daemonService.removeIntent(intentId);
        logger.d("Handling login response intent ("+intentId+").");

        String content = intent.getStringExtra(DaemonActionNames.CONTENT);
        if(content.equalsIgnoreCase(DaemonActionNames.CONTENT_OK)) {
            // login ok
            Game.getInstance().waitingForOpponent(loginData.getNick());
            tmpPlayerName = loginData.getNick();
            loginActivity.displayMainView();
            logged = true;
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
		String intentId = intent.getStringExtra(DaemonActionNames.ID);
		daemonService.removeIntent(intentId);
        logger.d("Handling new game response intent ("+intentId+").");
		
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
                // todo: wait for my turn
                gameActivity.getClientDaemonService().waitForNewTurn();
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
        String intentId = intent.getStringExtra(DaemonActionNames.ID);
		daemonService.removeIntent(intentId);
		logger.d("Handling end turn response intent ("+intentId+").");

        ErrorCode errorCode = (ErrorCode) intent.getSerializableExtra(DaemonActionNames.ERR_CODE);
        if(errorCode == ErrorCode.NO_ERROR) {
            // check if the game hasn't ended
            Serializable content = intent.getSerializableExtra(DaemonActionNames.CONTENT);
            if(content instanceof EndGameReceivedMessage) {
                logger.d("End of the game!");
                endGame(((EndGameReceivedMessage) content).getContent());
            } else {
                logger.d("Turn ok.");
                // wait for new turn
                daemonService.waitForNewTurn();
            }
        } else {
            // todo: handle error

        }
    }

    /**
     * Handles new turn message.
     *
     * @param intent
     */
    public void  handleNewTurn(Intent intent) {
		String intentId = intent.getStringExtra(DaemonActionNames.ID);
		daemonService.removeIntent(intentId);
        logger.d("Handling new turn message intent ("+intentId+").");
		
        ErrorCode errorCode = (ErrorCode) intent.getSerializableExtra(DaemonActionNames.ERR_CODE);
        if (errorCode == ErrorCode.NO_ERROR) {
            // check if it's new turn or end game
            Serializable content = intent.getSerializableExtra(DaemonActionNames.CONTENT);
            if(content instanceof StartTurnReceivedMessage) {
                // new turn
                StartTurnReceivedMessage msg = (StartTurnReceivedMessage) content;
                newTurn(false, msg.getFirstPlayerStones(), msg.getSecondPlayerStones());
            } else if(content instanceof EndGameReceivedMessage) {
                // end game
                endGame(((EndGameReceivedMessage) content).getContent());
            } else {
                // todo: error
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

    /**
     * Ends the game, disconnects the tcpClient.
     */
    public void endGame(String winner) {
        // todo: proper handling
        logger.d("End of the game, winner is: "+winner);
        stopTimer();
        Game.getInstance().resetGame();

        // stop daemon service
        DaemonService ds = gameActivity.getClientDaemonService();
        ds.disconnect();
        logged = false;
        gameActivity.displayEndGame(winner);
    }

    /**
     * Should be called from login activity.
     * If the client is already logged, switched to game activity.
     */
    public void checkIsLogged() {
        if(logged) {
            loginActivity.displayMainView();
        }
    }

    public void stopTimer() {
        if(turnTimer == null) {
            return;
        }
        turnTimer.cancel();
        turnTimer = null;
    }

    public void startTimer() {
        if(turnTimer != null) {
            logger.w("Timer is already running!");
            return;
        }
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

    /**
     * Check the stone on field which was selected. If there's a stone on the field, the field is the last
     * and the palyer can move, leave button will be displayed.
     * display leave button.
     * @param fieldNumber
     */
    public void select(int fieldNumber) {
        if(!Game.getInstance().isMyTurn()) {
            logger.w("Not my turn.");
            return;
        }

        if(!Game.getInstance().isAlreadyMoved() && Game.getInstance().getCurrentPlayer().isStoneOnField(fieldNumber) && fieldNumber == Game.LAST_FIELD) {
            gameActivity.showLeaveButton();
        } else {
            gameActivity.hideLeaveButton();
        }
    }

    /**
     * Tries to move the stone of the current player on the fromField to toFiled.
     * If the stone is moved, true is returned and board view is updated.
     *
     * @param fromField
     * @param toField
     * @return
     */
    public boolean move(int fromField, int toField) {
        // check if the sticks were already thrown
        if(!Game.getInstance().alreadyThrown()) {
            logger.w("Sticks not thrown yet!");
            gameActivity.displayToast("Nejdříve hoď dřívky!");
            return false;
        }

        // check that the player hasn't moved in this turn yet
        if(Game.getInstance().isAlreadyMoved()) {
            logger.w("Player already moved his stone!");
            gameActivity.displayToast("V tomto kole už jsi hrál!");
            return false;
        }

        // check that the move will be equal to the thrown value
        if(!Game.getInstance().isMoveLengthOk(fromField, toField)) {
            logger.w("Wrong move!");
            gameActivity.displayToast("Můžeš táhnout jen o délku hodu!");
            return false;
        }

        // move stones and update board
        if(Game.getInstance().moveStone(fromField, toField)) {
            gameActivity.updateStones(Game.getInstance().getFirstPlayer().getStones(),
                    Game.getInstance().getSecondPlayer().getStones());

            gameActivity.displayToast("Kámen posunut z "+fromField+" na "+toField+".");

            if(Game.getInstance().currentPlayerOnLastField() && !Game.getInstance().isAlreadyMoved()) {
                gameActivity.showLeaveButton();
            } else {
                gameActivity.hideLeaveButton();
            }
            return true;
        } else {
            logger.w("Turn from "+fromField+" to "+toField+" not possible.");
        }

        return false;
    }

    /**
     * If the currently selected pawn is on the field 30, pawn can leave the board.
     */
    public void leaveBoard() {
        PlayerNum currentPlayer = Game.getInstance().getCurrentPlayerNum();

        if(!Game.getInstance().isMyTurn()) {
            logger.w("Not my turn.");
            gameActivity.displayToast("Nejsem na tahu!\n");
            return;
        }

        if(Game.getInstance().isAlreadyMoved()) {
            logger.w("Already moved.");
            gameActivity.displayToast("V tomto tahu už jsem hrál.\n");
            return;
        }

        Stone stone = gameActivity.getSelected();
        if(stone == null) {
            logger.w("No stone selected.");
            return;
        }

        if(stone.getPlayer() != currentPlayer.num) {
            logger.w("Selected pawn doesn't belong to the current player!");
            return;
        }

        if(stone.getField() != Game.LAST_FIELD) {
            logger.w("Selected pawn isn't on the last field!");
            return;
        }

        Game.getInstance().leaveBoard();
        gameActivity.displayToast("Kámen opustil hrací plochu.\n");
        logger.d("Pawn "+stone+" has leaved the game board");
        gameActivity.deselect();
        gameActivity.updateStones(Game.getInstance().getFirstPlayer().getStones(),
                Game.getInstance().getSecondPlayer().getStones());
        gameActivity.hideLeaveButton();
    }

    public String getTmpPlayerName() {
        return tmpPlayerName;
    }
}
