package mkz.mkz_semestralka.core.network.daemon;

/**
 * Action names to be used for intents sent by broadcast.
 *
 * Created on 20.04.2017.
 *
 * @author mkz
 */

public class DaemonActionNames {

    /**
     * Use this value for IntentFilter when registering broadcast receiver for client daemon.
     */
    public static final String DAEMON_FILTER = "CLIENT_DAEMON_MESSAGE";

    /**
     * Get value of this field from intent to decide what kind of message is being sent by daemon.
     */
    public static final String CLIENT_ACTION_NAME = "CLIENT_DAEMON_ACTION";

    /**
     * Daemon is broadcasting login response.
     */
    public static final String LOGIN_RESPONSE = "LOGIN_RESPONSE";

}
