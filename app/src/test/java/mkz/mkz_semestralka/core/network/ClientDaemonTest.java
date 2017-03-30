package mkz.mkz_semestralka.core.network;

import android.os.Handler;
import android.os.Looper;

import org.junit.Test;

import mkz.mkz_semestralka.core.message.received.AbstractReceivedMessage;

import static junit.framework.Assert.assertNotNull;

/**
 * Simple unit test for client daemon
 *
 * Created on 30.03.2017.
 * @author Zdenek Vales
 */

public class ClientDaemonTest {

    @Test
    public void testLogin() throws InterruptedException {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        final ClientDaemon clientDaemon = ClientDaemon.getInstance();
        clientDaemon.setMainHandler(mainHandler);
        clientDaemon.start();

        clientDaemon.login(new LoginData("nick", "ip", 1), new Runnable() {
            @Override
            public void run() {
                AbstractReceivedMessage msg =clientDaemon.getResponseToLastAction();
                assertNotNull("Response is null!", msg);
                clientDaemon.stopDaemon();
            }
        });

        clientDaemon.join();
    }
}
