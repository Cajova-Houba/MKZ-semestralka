package mkz.mkz_semestralka.core.network.daemon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.error.ErrorCode;
import mkz.mkz_semestralka.core.message.received.AbstractReceivedMessage;
import mkz.mkz_semestralka.core.message.received.ErrorReceivedMessage;
import mkz.mkz_semestralka.core.message.received.ReceivedMessageTypeResolver;
import mkz.mkz_semestralka.core.message.received.StartGameReceivedMessage;
import mkz.mkz_semestralka.core.network.LoginData;

/**
 * An android service wrapper for the daemon itself.
 *
 * Created by Zdenek Vales on 06.04.2017.
 */

public class ClientDaemonService extends Service implements DaemonService {

    private final static Logger logger = Logger.getLogger(ClientDaemonService.class);

    public static ClientDaemonService getInstance() {
        return instance;
    }

    private final IBinder mBinder = new LocalBinder();

    private ClientDaemon clientDaemon = new ClientDaemon();

    private static ClientDaemonService instance = new ClientDaemonService();

    public class LocalBinder extends Binder {
        public ClientDaemonService getService() {
            return ClientDaemonService.this;
        }
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private Context context;

    public ClientDaemonService() {
        clientDaemon.start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        logger.d("Creating new ClientDaemonService.");
//        clientDaemon.start();
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
    public void login(LoginData loginData) {
        logger.i("Login.");
        clientDaemon.login(loginData, new Runnable() {
            @Override
            public void run() {
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

                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
                manager.sendBroadcast(i);
            }
        });
    }

    @Override
    public void waitForStartGame() {
        logger.i("Wait for start game.");
        clientDaemon.waitForStartGame(new Runnable() {
            @Override
            public void run() {
                AbstractReceivedMessage msg = clientDaemon.getResponseToLastAction();
                StartGameReceivedMessage start = ReceivedMessageTypeResolver.isStartGame(msg);
                ErrorReceivedMessage err = ReceivedMessageTypeResolver.isError(msg);

                Intent i = new Intent(DaemonActionNames.DAEMON_FILTER);
                i.putExtra(DaemonActionNames.CLIENT_ACTION_NAME, DaemonActionNames.START_GAME_RESPONSE);
                if (start != null) {
                    // start game
                    // add nicks
                    i.putExtra(DaemonActionNames.CONTENT, start.getFirstNickname() + ";" + start.getSecondNickname());

                    // add no err
                    i.putExtra(DaemonActionNames.ERR_CODE, ErrorCode.NO_ERROR);
                } else {
                    // error occured
                    i.putExtra(DaemonActionNames.CONTENT, DaemonActionNames.CONTENT_ERR);
                    i.putExtra(DaemonActionNames.ERR_CODE, err.getContent().code);
                }

                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
                manager.sendBroadcast(i);
            }
        });
    }

    @Override
    public void endTurn() {

    }

    @Override
    public void exit() {

    }

    @Override
    public void stopDaemon() {
        clientDaemon.stopDaemon();
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
