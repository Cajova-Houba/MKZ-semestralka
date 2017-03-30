package mkz.mkz_semestralka.core.network;

/**
 * Possible states of ClientDaemon.
 * Note that those are not states of the thread itself!
 *
 * Created on 23.03.2017.
 * @author mkz
 */

public enum ClientDaemonState {

    /**
     * Daemon is idle and is waiting for something to happen.
     */
    IDLE,

    /**
     * Daemon is waiting for messages from server and keeps responding to them.
     */
    LISTEN,

    /**
     * Daemon will send the login message.
     */
    LOGIN,

    /**
     * Daemon will keep waiting for the login response.
     */
    LOGIN_RESPONSE_WAIT,

    /**
     * This will STOP the Daemon thread, use wisely!
     */
    STOP
}
