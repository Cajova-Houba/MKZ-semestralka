package mkz.mkz_semestralka.core.game;

/**
 * Created by Zdenek Vales on 23.03.2017.
 */
public enum GameState {

    /**
     * Game haven't started yet.
     */
    NOT_STARTED,

    /**
     * User is logged to server and is waiting for opponent.
     */
    WAITING_FOR_OPPONENT,

    /**
     * The game is running.
     */
    RUNNING,

    /**
     * The game has ended.
     */
    ENDED
}
