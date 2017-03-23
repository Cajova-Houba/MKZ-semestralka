package mkz.mkz_semestralka.core.network;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.valesz.ups.common.message.received.AbstractReceivedMessage;
import org.valesz.ups.common.message.received.ExpectedMessageComparator;

import java.net.Socket;

/**
 * A service which will create a new PreStartReceiver.
 *
 * Use this to receive messages before the game has started.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class PreStartReceiverService extends Service<AbstractReceivedMessage> {

    private Socket socket;

    private ExpectedMessageComparator expectedMessageComparator;

    private int maxTimeoutMs;

    private int maxAttempts;

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setExpectedMessageComparator(ExpectedMessageComparator expectedMessageComparator) {
        this.expectedMessageComparator = expectedMessageComparator;
    }

    public void setMaxTimeoutMs(int maxTimeoutMs) {
        this.maxTimeoutMs = maxTimeoutMs;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    protected Task<AbstractReceivedMessage> createTask() {
        return new PreStartReceiver(socket, expectedMessageComparator, maxTimeoutMs, maxAttempts);
    }
}
