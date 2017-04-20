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
     * Either OK or ERR messages are expected, so one of those will be stored in responseToLastAction variable.
     *
     * @param loginData Login data.
     */
    void login(LoginData loginData);

    /**
     * Sends an exit message to the server. No callback is probably needed for this.
     */
    void exit();

    /**
     * Immediately after the current state code is done stops the daemon.
     */
    void stopDaemon();
}
