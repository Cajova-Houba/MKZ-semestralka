package mkz.mkz_semestralka.core.error;

/**
 * Exception thrown if the player tries to end turn when he's not the one playing.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class NotMyTurnException extends Exception {

    public NotMyTurnException() {
    }

    public NotMyTurnException(String message) {
        super(message);
    }
}
