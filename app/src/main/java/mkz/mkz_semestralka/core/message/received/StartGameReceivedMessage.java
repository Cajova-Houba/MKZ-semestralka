package mkz.mkz_semestralka.core.message.received;


import mkz.mkz_semestralka.core.message.MessageType;

/**
 * START_GAME message with two nicks.
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class StartGameReceivedMessage extends AbstractReceivedMessage<String[]> {

    private String nick1;
    private String nick2;

    public StartGameReceivedMessage(String nick1, String nick2) {
        this.nick1 = nick1;
        this.nick2 = nick2;
    }

    public String getFirstNickname() {
        return nick1;
    }

    public String getSecondNickname() {
        return nick2;
    }

    @Override
    public String[] getContent() {
        return new String[] {nick1, nick2};
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.INF;
    }
}
