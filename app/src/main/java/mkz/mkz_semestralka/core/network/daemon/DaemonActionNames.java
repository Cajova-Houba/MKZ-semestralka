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
     * Name of the field which will contain content of the response.
     */
    public static final String CONTENT = "DAEMON_RESPONSE_CONTENT";

    /**
     * Value of the content field if everything is ok.
     */
    public static final String CONTENT_OK = "OK";

    /**
     * Value of the content field if error occurs.
     */
    public static final String CONTENT_ERR = "ERR";

    /**
     * Name of the field which will contain error code, if error occurs.
     * The filed will contain an ErrorCode object, not a string.
     */
    public static final String ERR_CODE = "ERR_CODE";

    /**
     * Daemon is broadcasting login response.
     */
    public static final String LOGIN_RESPONSE = "LOGIN_RESPONSE";

    /**
     * Daemon is broadcasting start game response.
     */
    public static final String START_GAME_RESPONSE = "START_GAME_RESPONSE";


}
