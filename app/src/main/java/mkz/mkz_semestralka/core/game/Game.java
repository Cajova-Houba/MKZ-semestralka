package mkz.mkz_semestralka.core.game;

import java.util.Random;

import mkz.mkz_semestralka.core.Constrains;
import mkz.mkz_semestralka.core.Logger;

/**
 * This class contains the game data and implements game logic.
 *
 * Created by Zdenek Vales on 23.03.2017.
 */

public class Game {

    public static final Logger logger = Logger.getLogger(Game.class);

    public static final int LAST_FIELD = 30;
    public static final int OUT_OF_BOARD = 31;

    private static Game instance;

    public static Game getInstance() {
        if(instance == null) {
            instance = new Game();
        }

        return instance;
    }



    /**
     * For testing purposes.
     */
    public static final int[] WINNER = new int[] {OUT_OF_BOARD, OUT_OF_BOARD, OUT_OF_BOARD, OUT_OF_BOARD, OUT_OF_BOARD};

    /**
     * Nickname of players which controls this client.
     * Used as temporal storage.
     */
    private String me;

    /**
     * Number of the player which controls this client.
     */
    private PlayerNum myPlayer;

    /**
     * The player which starts the game.
     */
    private Player firstPlayer;

    /**
     * The second player.
     */
    private Player secondPlayer;

    /**
     * True if the winner is me, false if the opponent has won.
     */
    private boolean winner;

    /**
     * Current state of the game.
     */
    private GameState state;

    /**
     * Whose turn currently is.
     */
    private PlayerNum turn;

    /**
     * Player's value. It's possible to throw only once per turn.
     * Default value is -1.
     *
     */
    private int thrownValue;

    /**
     * True if the player already moved a stone in this turn.
     */
    private boolean alreadyMoved;

    /**
     * Flag that indicates that the player can throw sticks again.
     */
    private boolean throwAgain;


    private Game() {
        resetGame();
    }

    public void resetGame() {
        winner = false;
        state = GameState.NOT_STARTED;
        turn = PlayerNum.PLAYER_1;
        thrownValue = -1;
        alreadyMoved = false;
        throwAgain = false;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(Player secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public boolean isWinner() {
        return winner;
    }

    public GameState getState() {
        return state;
    }

    public boolean isMyTurn() {
        return turn == myPlayer;
    }

    /**
     * Changes the state to WAITING_FOR_OPPONENT
     *
     * @param me Nick of the player which controls this client.
     *
     */
    public void waitingForOpponent(String me) {
        state = GameState.WAITING_FOR_OPPONENT;
        this.me = me;
    }

    /**
     * Starts the new game.
     * Sets the state to RUNNING.
     * Initializes both player objects.
     *
     *
     * @param firstPlayer The player who starts the game.
     * @param secondPlayer
     */
    public void startGame(String firstPlayer, String secondPlayer) {
        if (firstPlayer.equals(me)) {
            logger.d(firstPlayer+" is same as  "+me);
            myPlayer = PlayerNum.PLAYER_1;
        } else {
            logger.d(firstPlayer+" is not same as  "+me);
            myPlayer = PlayerNum.PLAYER_2;
        }

        this.turn = PlayerNum.PLAYER_1;
        thrownValue = -1;
        alreadyMoved = false;
        throwAgain = false;

        this.firstPlayer = new Player(firstPlayer,Player.FIRST_PLAYER_INIT_POS);
        this.secondPlayer = new Player(secondPlayer,Player.SECOND_PLAYER_INIT_POS);

        this.state = GameState.RUNNING;
    }

    /**
     * Starts a new turn and updates the stones.
     * @param firstPlayerStones
     * @param secondPlayerStones
     */
    public void newTurn(int[] firstPlayerStones, int[] secondPlayerStones) {
        thrownValue = -1;
        alreadyMoved = false;
        turn = turn == PlayerNum.PLAYER_1 ? PlayerNum.PLAYER_2 : PlayerNum.PLAYER_1;

        firstPlayer.setStones(firstPlayerStones);
        secondPlayer.setStones(secondPlayerStones);
    }

    /**
     * Ends my turn.
     */
    public void endTurn() {
        turn = turn == PlayerNum.PLAYER_1 ? PlayerNum.PLAYER_2 : PlayerNum.PLAYER_1;
    }

    /**
     * Throws the sticks and returns thrown value. If the sticks had been already thrown,
     * actual value is returned.
     *
     * Probabilities:
     * 1: 25%
     * 2: 37.5%
     * 3: 25%
     * 4: 6.25%
     * 5: 6.25%
     *
     *
     * @return
     */
    public int throwSticks() {
        if(thrownValue == -1 || throwAgain) {
            Random random = new Random();
            double tmp;
            tmp = random.nextDouble()*100;
            if(tmp < 37.5) {
                thrownValue = 2;
                throwAgain = false;
            } else if (tmp < 65.5) {
                thrownValue = 1;
                throwAgain = false;
            } else if (tmp < 87.5) {
                thrownValue = 3;
                throwAgain = false;
            } else if (tmp < 93.75) {
                thrownValue = 4;
                throwAgain = true;
            } else {
                thrownValue = 5;
                throwAgain = true;
            }
        }

        return thrownValue;
    }

    /**
     * Moves a current player's turn from one field to another.
     * If the turn isn't possible, false is returned. Otherwise, true
     * is returned and alreadyMoved is set to true.
     *
     * Both fields are expected to be already checked.
     *
     * If the toFiled is empty, stone will be moved.
     * If there's a opponent's stone on the toField and the switch is
     * possible, stones will be switched.
     * In any other case, false will be returned.
     *
     * @param fromField
     * @param toFiled
     * @return
     */
    public boolean moveStone(int fromField, int toFiled) {

        Player player = getCurrentPlayer();
        Player other = getOtherPlayer();
        if(isFieldEmpty(toFiled)) {
            // move the players stone from one field to another empty field
            player.moveStone(fromField, toFiled);
            if(!canThrowAgain()) {
                alreadyMoved = true;
            }
            return true;
        } else {
            // if there's a stone, check that it's opponent's stone and the switch is possible
            if(other.isStoneOnField(toFiled)) {
                // check that the switch is possible
                // switch is possible if there aren't two opponent's stones next to each other
                if (!other.isStoneOnField(toFiled -1) && !other.isStoneOnField(toFiled+1)) {
                    //switch stones
                    player.moveStone(fromField, toFiled);
                    other.moveStone(toFiled, fromField);
                    if(!canThrowAgain()) {
                        alreadyMoved = true;
                    }
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if the state is RUNNING.
     * @return
     */
    public boolean isRunning() {
        return state == GameState.RUNNING;
    }

    /**
     * Returns true if the player which controls this client begins the game.
     * @return
     */
    public boolean amIFirst() {
        return myPlayer == PlayerNum.PLAYER_1;
    }

    /**
     * Returns a nick of player who currently turns.
     * @return
     */
    public String getCurrentPlayerNick() {
        return turn == PlayerNum.PLAYER_1 ? firstPlayer.getNick() : secondPlayer.getNick();
    }

    /**
     * Returns the number of the current player.
     * @return
     */
    public PlayerNum getCurrentPlayerNum() {
        return turn;
    }

    /**
     * Returns current player.
     * @return
     */
    public Player getCurrentPlayer() {
        return turn == PlayerNum.PLAYER_1 ? firstPlayer : secondPlayer;
    }

    /**
     * Returns the other player.
     * @return
     */
    public Player getOtherPlayer() {
        return turn == PlayerNum.PLAYER_1 ? secondPlayer : firstPlayer;
    }

    public PlayerNum getMyPlayerNum() {
        return myPlayer;
    }

    public int getThrownValue() {
        return thrownValue;
    }

    /**
     * Only for testing!
     * @return
     */
    public void setThrownValue(int newValue) {
        this.thrownValue = newValue;
    }

    /**
     * Returns true if the sticks had been already thrown in this turn.
     * @return
     */
    public boolean alreadyThrown() {
        return thrownValue != -1;
    }

    /**
     * Returns true if a player already moved a stone in this turn.
     * @return
     */
    public boolean isAlreadyMoved() {
        return alreadyMoved;
    }

    /**
     * Checks if the move from one field to another is ok with
     * thrown value.
     * @return
     */
    public boolean isMoveLengthOk(int from, int to) {
        if(!alreadyThrown()) {
            return false;
        }

        return Math.abs(from - to) == getThrownValue();
    }

    /**
     * Returns true if there is no stone on the field.
     * @param field
     * @return
     */
    public boolean isFieldEmpty(int field) {
        boolean empty = true;
        for (int s : firstPlayer.getStones()) {
            empty = empty && (s != field);
        }

        for (int s : secondPlayer.getStones()) {
            empty = empty && (s != field);
        }

        return empty;
    }

    /**
     * Returns true if the player can throw again.
     * @return
     */
    public boolean canThrowAgain() {
        return throwAgain;
    }

    /**
     * Removes the stone which is currently on the field 30 out of board.
     */
    public void leaveBoard() {
        int[] p1Stones = getFirstPlayer().getStones();
        int[] p2Stones = getSecondPlayer().getStones();
        for (int i = 0; i < Constrains.MAX_NUMBER_OF_STONES; i++) {
            if(p1Stones[i] == LAST_FIELD) {
                p1Stones[i] = OUT_OF_BOARD;
                if(!canThrowAgain()) {
                    alreadyMoved = true;
                }
                return;
            }

            if(p2Stones[i] == LAST_FIELD) {
                p2Stones[i] = OUT_OF_BOARD;
                if(!canThrowAgain()) {
                    alreadyMoved = true;
                }
                return;
            }
        }
    }

    /**
     * Returns true if one of the current player's stone is
     * on the last field.
     * @return
     */
    public boolean currentPlayerOnLastField() {
        PlayerNum cp = getCurrentPlayerNum();
        int[] stones;
        if(cp == PlayerNum.PLAYER_1) {
            stones = getFirstPlayer().getStones();
        } else {
            stones = getSecondPlayer().getStones();
        }

        for (int i = 0; i < stones.length; i++) {
            if(stones[i] == LAST_FIELD) {
                return true;
            }
        }

        return false;
    }

}
