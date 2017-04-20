package mkz.mkz_semestralka.core.network.daemon;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import mkz.mkz_semestralka.core.Logger;
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
    public void login(LoginData loginData, final Handler mainHandler) {
        clientDaemon.login(loginData, new Runnable() {
            @Override
            public void run() {
                mainHandler.sendEmptyMessage(1);
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
