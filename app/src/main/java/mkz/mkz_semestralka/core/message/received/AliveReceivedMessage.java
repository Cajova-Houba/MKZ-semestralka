package mkz.mkz_semestralka.core.message.received;

import mkz.mkz_semestralka.core.message.MessageType;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class AliveReceivedMessage extends AbstractReceivedMessage {

    @Override
    public Object getContent() {
        return "ALIVE";
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.INF;
    }
}
