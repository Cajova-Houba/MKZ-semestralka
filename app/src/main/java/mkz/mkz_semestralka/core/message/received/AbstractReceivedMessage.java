package mkz.mkz_semestralka.core.message.received;

import java.io.Serializable;

import mkz.mkz_semestralka.core.message.MessageType;

/**
 * A base class for all responses received from server.
 *
 * T defines the data type of response content. Usually just a String.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public abstract class   AbstractReceivedMessage<T> implements Serializable {

    /**
     * Returns a message content.
     * @return
     */
    public abstract T getContent();

    /**
     * Returns a type of response.
     * @return
     */
    public abstract MessageType getMessageType();

    @Override
    public String toString() {
        String cnt = getContent() == null ? "null" : getContent().toString();
        String msgType = getMessageType() == null ? "null" : getMessageType().toString();
        return String.format("Content: %s, MessageType: %s.", cnt, msgType);
    }
}
