package mkz.mkz_semestralka.core.message.received;


import mkz.mkz_semestralka.core.message.MessageType;

/**
 * Message from server that indicates beginning of the new turn.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class StartTurnReceivedMessage extends AbstractReceivedMessage<int[]> {

    private int[] firstPlayerStones;
    private int[] secondPlayerStones;

    public StartTurnReceivedMessage(int[] firstPlayerStones, int[] secondPlayerStones) {
        this.firstPlayerStones = firstPlayerStones;
        this.secondPlayerStones = secondPlayerStones;
    }

    public int[] getFirstPlayerStones() {
        return firstPlayerStones;
    }

    public int[] getSecondPlayerStones() {
        return secondPlayerStones;
    }

    /**
     * Returns both sets of stones.
     * @return
     */
    @Override
    public int[] getContent() {
        int[] both = new int[firstPlayerStones.length + secondPlayerStones.length];
        for (int i = 0; i < firstPlayerStones.length; i++) {
            both[i] = firstPlayerStones[i];
            both[i+firstPlayerStones.length] = secondPlayerStones[i];
        }

        return both;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CMD;
    }
}
