package mkz.mkz_semestralka.core.network.daemon;

import mkz.mkz_semestralka.core.error.Error;
import mkz.mkz_semestralka.core.message.received.AbstractReceivedMessage;
import mkz.mkz_semestralka.core.network.LoginData;

/**
 * This interface specifies operations which can be used to control the daemon thread.
 * There is an 'infinite' loop in the daemon and methods specified here are able to change the
 * state of the state machine inside this infinite loop.
 *
 * If the rest of the application needs a response from server, callback will be passed in the method
 * and response getter method can then be used in this callback to obtain this response.
 *
 *
 * Created on 30.03.2017.
 * @author Zdenek Vales
 */

public interface Daemon {

    /**
     * Last error which occurred. Use this for handling communication errors.
     * When error occurs, daemon will switch to idle state so it won't be rewritten.
     *
     * @return
     */
    Error getLastError();

    /**
     * User should use this after the last error is no longer needed.
     */
    void nullLastError();

    /**
     * Returns the response to the last action performed.
     * @return Response to the last message sent to the server. Can be null if no response has been received
     * or no action was performed.
     */
    AbstractReceivedMessage getResponseToLastAction();

    /**
     * Daemon will send the login message to the server and then waits for the response.
     * After nick response is received, callback is called. Either OK or ERR messages are expected,
     * so one of those will be stored in responseToLastAction variable.
     *
     * @param loginData Login data.
     * @param callback A piece of code to be executed by main thread after the response to login message is received.
     */
    void login(LoginData loginData, Runnable callback);

    /**
     * Switch the daemon state to WAIT_FOR_START_GAME. After the message is received, callback is called.
     * Either error message or start game message is stored in responseToLastAction.
     *
     * @param callback A piece of code to be executed by main thread after the error or start game message is received.
     */
    void waitForStartGame(Runnable callback);

    /**
     * Sends an exit message to the server. No callback is probably needed for this.
     */
    void exit();

    /**
     * Immediately after the current state code is done stops the daemon.
     */
    void stopDaemon();

    /**
     * Sends the end turn message and the daemon will wait for message to start next turn.
     */
    void endTurn(int[] firstPlayerStones, int[] secondPlayerStones, Runnable callback);

    /**
     * Daemon will wait for new turn message and when it's received, callback is called.
     * @param callback
     */
    void waitForNewTurn(Runnable callback);

    /**
     * Disconnect the daemon and switch to to the IDLE state.
     */
    void disconnect();

}
