package mkz.mkz_semestralka.core.message;

import mkz.mkz_semestralka.core.Constrains;

/**
 * Builder class for Messages so that the life is easier.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */

public class MessageBuilder {

    /**
     * Creates a message of type CMD with content where the
     * first byte is length of the nick and the rest of the content is the nick
     * itself.
     * @param nick Player's nick.
     * @return
     */
    public static Message createNickMessage(String nick) {
        nick = Integer.toString(nick.length()).charAt(0) + nick;
        return new Message(MessageType.CMD, nick);
    }

    public static Message createOKMessage(){return new Message(MessageType.INF, "OK");}

    /**
     * Creates a message of type INF with EXIT string as a content.
     * @return Message object with exit message data.
     */
    public static Message createExitMessage() {
        return new Message(MessageType.INF, "EXIT");
    }

    public static Message createIsAliveMessage() {return new Message(MessageType.INF, "ALIVE");}

    /**
     * Creates end turn message with both turn words.
     * @param p1TurnWord Turn word of player 1.
     * @param p2TurnWord Turn word of player 2.
     * @return Message object with end turn message data.
     */
    public static Message createEndTurnMessage(int[] p1TurnWord, int[] p2TurnWord) {
        StringBuilder turnWordBuilder = new StringBuilder();
        for(int i = 0; i < Constrains.MAX_NUMBER_OF_STONES; i++) {
            turnWordBuilder.append(String.format("%02d",p1TurnWord[i]));
        }
        for(int i = 0; i < Constrains.MAX_NUMBER_OF_STONES; i++) {
            turnWordBuilder.append(String.format("%02d",p2TurnWord[i]));
        }
        return new Message(MessageType.INF,turnWordBuilder.toString());
    }

}
