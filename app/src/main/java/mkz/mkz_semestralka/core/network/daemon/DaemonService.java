package mkz.mkz_semestralka.core.network.daemon;

import mkz.mkz_semestralka.core.network.LoginData;

/**
 * Methods to be used to communicate with the daemon are specified here.
 *
 * Created by Zdenek Vales on 06.04.2017.
 */
public interface DaemonService {

    /**
     * Daemon will send the login message to the server and then waits for the response.
     * After nick response is received, message will be broadcast with action name DaemonActionNames.DAEMON_FILTER.
     * If the login is OK, DaemonActionNames.CONTENT field of intent will contain "OK", otherwise error code will be stored in this field.
     *
     * @param loginData Login data.
     */
    void login(LoginData loginData);

    /**
     * Daemon will keep receiving message from server and when the start turn message is received,
     * message will be broadcast.
     *
     * DaemonActionNames.START_GAME_RESPONSE will be broadcast. The CONTENT field will contain names of two players separated by ';'.
     * If error occurs, CONTENT will contain CONTENT_ERR.
     *
     */
    void waitForStartGame();

    /**
     * Sends the end turn message and the daemon will wait for message to start next turn.
     *
     * After response to end turn message is received, message with following values will be broadcast:
     * CLIENT_ACTION_NAME: END_TURN_RESPONSE
     * CONTENT: OK / ERR
     * ERR_CODE: NO_ERROR / error code
     *
     */
    void endTurn(int[] firstPlayerStones, int[] secondPlayerStones);

    /**
     * Daemon will keep waiting for new turn message.
     *
     * When it's received, message with following values wil be broadcast.
     * CLIENT_ACTION_NAME: NEW_TURN_MESSAGE
     * CONTENT: StartTurnReceivedMessage, EndGameReceivedMessage, ERR
     * ERR_CODE: NO_ERROR / error code
     */
    void waitForNewTurn();

    /**
     * Sends an exit message to the server. No callback is probably needed for this.
     */
    void exit();

    /**
     * Immediately after the current state code is done stops the daemon.
     */
    void stopDaemon();
}
