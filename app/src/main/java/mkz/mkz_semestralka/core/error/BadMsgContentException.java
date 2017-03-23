package mkz.mkz_semestralka.core.error;

/**
 * Thrown when the message is received, but its content is malformed.
 *
 * Created on 23.03.2017.
 * @author Zdenek Valess
 */
public class BadMsgContentException extends ReceivingException {

    public BadMsgContentException() {
        super(Error.BAD_MSG_CONTENT());
    }
}
