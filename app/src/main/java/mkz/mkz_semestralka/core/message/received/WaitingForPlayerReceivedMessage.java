package mkz.mkz_semestralka.core.message.received;


import mkz.mkz_semestralka.core.message.MessageType;

/**
 * Message containing the nick of the player the game is waiting for to reconnect.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class WaitingForPlayerReceivedMessage extends AbstractReceivedMessage<String>{

    private String nick;

    public WaitingForPlayerReceivedMessage(String nick) {
        this.nick = nick;
    }

    @Override
    public String getContent() {
        return nick;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.INF;
    }
}
