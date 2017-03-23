package mkz.mkz_semestralka.core.message.received;

import mkz.mkz_semestralka.core.message.MessageType;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class EndGameReceivedMessage extends AbstractReceivedMessage<String> {

    private String winner;

    public EndGameReceivedMessage(String winner) {
        this.winner = winner;
    }

    /**
     * Returns the winner of the game.
     * If the returned content is empty, no-one wins and the game
     * has ended because of some error.
     * @return
     */
    @Override
    public String getContent() {
        return winner;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.INF;
    }
}
