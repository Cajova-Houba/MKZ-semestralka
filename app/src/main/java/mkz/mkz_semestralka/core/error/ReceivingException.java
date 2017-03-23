package mkz.mkz_semestralka.core.error;

import java.lang.*;

/**
 * Exception thrown while receiving a message.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class ReceivingException extends Exception {

    public final Error error;

    public ReceivingException(Error error) {
        this.error = error;
    }
}
