package mkz.mkz_semestralka.core.error;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class EndOfStreamReached extends ReceivingException{

    public EndOfStreamReached() {
        super(Error.NO_CONNECTION());
    }
}
