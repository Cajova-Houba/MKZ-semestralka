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
     * Daemon is idle and just keeps receiving messages and responding to them.
     */
    IDLE
}
