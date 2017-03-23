package mkz.mkz_semestralka.core.network;

/**
 * A daemon thread which will fetch communication with the server.
 * The main thread will work only with UI, everything else should be done by daemon thread.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */

public class ClientDaemon extends Thread{

    public static ClientDaemon getInstance() {
        if(instance == null) {
            instance = new ClientDaemon();
        }

        return instance;
    }

    /**
     * Nulls the current instance of the thread.
     * Should be called ONLY after the thread has finished.
     */
    public static void nullInstance() {
        instance = null;
    }

    private static ClientDaemon instance;

    private ClientDaemonState state;

    @Override
    public void run() {
        super.run();
    }

    /**
     * Idle state. The daemon will keep receiving common messages (alive, error...) and responding to them
     *
     */
    private void idleState() {
        while(state == ClientDaemonState.IDLE) {

        }
    }
}
