package mkz.mkz_semestralka.core.network.daemon;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.error.ErrorCode;
import mkz.mkz_semestralka.core.message.received.AbstractReceivedMessage;
import mkz.mkz_semestralka.core.message.received.ErrorReceivedMessage;
import mkz.mkz_semestralka.core.message.received.ReceivedMessageTypeResolver;
import mkz.mkz_semestralka.core.network.LoginData;

/**
 * An android service wrapper for the daemon itself.
 *
 * Created by Zdenek Vales on 06.04.2017.
 */

public class ClientDaemonService extends Service implements DaemonService {

    private final static Logger logger = Logger.getLogger(ClientDaemonService.class);

    private final IBinder mBinder = new LocalBinder();

    private ClientDaemon clientDaemon = new ClientDaemon();

    public class LocalBinder extends Binder {
        public ClientDaemonService getService() {
            return ClientDaemonService.this;
        }
    }

//    private final class ServiceHandler extends Handler {
//        public ServiceHandler(Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        logger.d("Creating new ClientDaemonService.");
        clientDaemon.start();
    }

    @Override
    public void onDestroy() {
        clientDaemon.stopDaemon();
        try {
            clientDaemon.join();
        } catch (InterruptedException e) {
            logger.e("Exception while joining the client daemon thread! "+e.getMessage());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void login(LoginData loginData) {;
        clientDaemon.login(loginData, new Runnable() {
            @Override
            public void run() {
                // todo: move this to controller?
                AbstractReceivedMessage msg = clientDaemon.getResponseToLastAction();
                ErrorReceivedMessage err = ReceivedMessageTypeResolver.isError(msg);

                Intent i = new Intent(DaemonActionNames.DAEMON_FILTER);
                i.putExtra(DaemonActionNames.CLIENT_ACTION_NAME, DaemonActionNames.LOGIN_RESPONSE);

                // add response from server to broadcast message
                if (msg != null) {
                    if (ReceivedMessageTypeResolver.isOk(msg) != null) {
                        i.putExtra(DaemonActionNames.CONTENT, DaemonActionNames.CONTENT_OK);
                        i.putExtra(DaemonActionNames.ERR_CODE, ErrorCode.NO_ERROR);
                    } else if (err != null) {
                        i.putExtra(DaemonActionNames.CONTENT, DaemonActionNames.CONTENT_ERR);
                        i.putExtra(DaemonActionNames.ERR_CODE, err.getContent().code);
                    }
                }

                sendBroadcast(i);
            }
        });
    }

    @Override
    public void exit() {

    }

    @Override
    public void stopDaemon() {
        clientDaemon.stopDaemon();
    }
}
