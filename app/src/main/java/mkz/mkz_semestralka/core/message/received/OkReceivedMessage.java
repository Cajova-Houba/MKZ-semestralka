package mkz.mkz_semestralka.core.message.received;


import mkz.mkz_semestralka.core.message.MessageType;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class OkReceivedMessage extends AbstractReceivedMessage<String>{

    @Override
    public String getContent() {
        return "OK";
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.INF;
    }
}
