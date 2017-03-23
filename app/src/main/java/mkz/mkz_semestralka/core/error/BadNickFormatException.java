package mkz.mkz_semestralka.core.error;

/**
 * Thrown when a message should contain nick, but the nick is malformed.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class BadNickFormatException extends ReceivingException {

    public BadNickFormatException() {
        super(Error.BAD_NICK());
    }
}
