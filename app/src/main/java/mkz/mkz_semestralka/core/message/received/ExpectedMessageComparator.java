package mkz.mkz_semestralka.core.message.received;

/**
 * A simple comparator for received messages.
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public interface ExpectedMessageComparator {

    /**
     * Returns true if the message is the expected one.
     * @param message Received message. May be null.
     * @return
     */
    public boolean isExpected(AbstractReceivedMessage message);
}
