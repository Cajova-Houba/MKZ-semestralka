package mkz.mkz_semestralka.core.error;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class MaxAttemptsReached extends ReceivingException{

    public MaxAttemptsReached() {
        super(Error.MAX_ATTEMPTS());
    }
}
