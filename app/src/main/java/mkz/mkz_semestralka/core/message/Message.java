package mkz.mkz_semestralka.core.message;

/**
 * Message which will be sent to server.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class Message {

    private final MessageType messageType;
    private final String content;

    public Message(MessageType messageType, String content) {
        this.messageType = messageType;
        this.content = content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public int getMessageLength() {
        return content.length();
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", content='" + content + '\'' +
                '}';
    }

    /**
     * Returns true if the message type is error.
     * @return
     */
    public boolean isError() {
        return messageType == MessageType.ERR;
    }

    /**
     * Returns true if the message is INFOK.
     * @return
     */
    public boolean isOk() {
        return messageType == MessageType.INF && content.equals("OK");
    }

    /**
     * Returns this message as an array of bytes.
     * First four bytes are length of the whole message.
     * @return
     */
    public byte[] toBytes() {
        String msg = messageType.name() + content;
        byte[] res = new byte[msg.length()+1];

        for (int i = 0; i < msg.length(); i++) {
            res[i] = (byte)msg.charAt(i);
        }
        res[msg.length()] = '\n';

        return res;
    }

}
