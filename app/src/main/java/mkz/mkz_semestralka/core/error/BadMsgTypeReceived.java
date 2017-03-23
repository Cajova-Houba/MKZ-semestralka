package mkz.mkz_semestralka.core.error;

/**
 * Exception thrown when an unrecognizable message type is received.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class BadMsgTypeReceived extends ReceivingException{

    public BadMsgTypeReceived() {
        super(Error.BAD_MSG_TYPE());
    }
}
