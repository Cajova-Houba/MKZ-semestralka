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
import mkz.mkz_semestralka.core.error.Error;
import mkz.mkz_semestralka.core.error.ErrorCode;
import mkz.mkz_semestralka.core.message.received.AbstractReceivedMessage;
import mkz.mkz_semestralka.core.message.received.EndGameReceivedMessage;
import mkz.mkz_semestralka.core.message.received.ErrorReceivedMessage;
import mkz.mkz_semestralka.core.message.received.OkReceivedMessage;
import mkz.mkz_semestralka.core.message.received.ReceivedMessageTypeResolver;
import mkz.mkz_semestralka.core.message.received.StartGameReceivedMessage;
import mkz.mkz_semestralka.core.message.received.StartTurnReceivedMessage;
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

//    private ThreadClientDaemon clientDaemon = new ClientDaemon();
//    private ThreadClientDaemon clientDaemon = new MockClientDaemon();
    private ThreadClientDaemon clientDaemon = new TCPClientDaemon();

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
	
	/**
	 * After every broadcast, the intent will be saved to this queue and will be removed (by it's unique ID) when it's handled.
	 * Used for storing pending messages from daemon.
	 */
	private List<Intent> intentQueue;

    public ClientDaemonService() {
		intentQueue = new LinkedList<>();
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
                OkReceivedMessage okMsg = ReceivedMessageTypeResolver.isOk(msg);
                ErrorReceivedMessage err = ReceivedMessageTypeResolver.isError(msg);
                Error internalError = clientDaemon.getLastError();

                Intent i = createIntent(DaemonActionNames.LOGIN_RESPONSE);

                // handle internal error first
                if(internalError != null) {
                    i.putExtra(DaemonActionNames.CONTENT, DaemonActionNames.CONTENT_ERR);
                    i.putExtra(DaemonActionNames.ERR_CODE, internalError.code);
                }

                // now handle messages
                else if (okMsg != null) {
                    i.putExtra(DaemonActionNames.CONTENT, DaemonActionNames.CONTENT_OK);
                    i.putExtra(DaemonActionNames.ERR_CODE, ErrorCode.NO_ERROR);
                } else {
                    i.putExtra(DaemonActionNames.CONTENT, DaemonActionNames.CONTENT_ERR);
                    i.putExtra(DaemonActionNames.ERR_CODE, err.getContent().code);
                }

                broadcastIntent(i);
            }
        });
    }

    @Override
    public void waitForStartGame() {
        logger.i("Wait for start game.");
        clientDaemon.waitForStartGame(new Runnable() {
            @Override
            public void run() {
                Intent i = createIntent(DaemonActionNames.START_GAME_RESPONSE);

                Error internalError = clientDaemon.getLastError();
                AbstractReceivedMessage msg = clientDaemon.getResponseToLastAction();
                StartGameReceivedMessage start = ReceivedMessageTypeResolver.isStartGame(msg);
                ErrorReceivedMessage err = ReceivedMessageTypeResolver.isError(msg);

                // handle internal error first
                if(internalError != null) {
                    i.putExtra(DaemonActionNames.CONTENT, DaemonActionNames.CONTENT_ERR);
                    i.putExtra(DaemonActionNames.ERR_CODE, internalError.code);
                }

                // now handle messages
                else if (start != null) {
                    // start game
                    // add nicks
                    i.putExtra(DaemonActionNames.CONTENT, start.getFirstNickname() + ";" + start.getSecondNickname());
                    i.putExtra(DaemonActionNames.ERR_CODE, ErrorCode.NO_ERROR);
                } else {
                    // error occured
                    i.putExtra(DaemonActionNames.CONTENT, DaemonActionNames.CONTENT_ERR);
                    i.putExtra(DaemonActionNames.ERR_CODE, err.getContent().code);
                }

                broadcastIntent(i);
            }
        });
    }

    @Override
    public void endTurn(int[] firstPlayerStones, int[] secondPlayerStones) {
        clientDaemon.endTurn(firstPlayerStones, secondPlayerStones, new Runnable() {
            @Override
            public void run() {
                AbstractReceivedMessage msg = clientDaemon.getResponseToLastAction();
                OkReceivedMessage okMsg = ReceivedMessageTypeResolver.isOk(msg);
                EndGameReceivedMessage endGame = ReceivedMessageTypeResolver.isEndGame(msg);
                ErrorReceivedMessage err = ReceivedMessageTypeResolver.isError(msg);

                Intent i = createIntent(DaemonActionNames.END_TURN_RESPONSE);
                if(okMsg != null) {
                    // turn ok
                    i.putExtra(DaemonActionNames.CONTENT, DaemonActionNames.CONTENT_OK);
                    i.putExtra(DaemonActionNames.ERR_CODE, ErrorCode.NO_ERROR);
                } else if (endGame != null) {
                    // end game
                    i.putExtra(DaemonActionNames.CONTENT, endGame);
                    i.putExtra(DaemonActionNames.ERR_CODE, ErrorCode.NO_ERROR);
                } else {
                    // not ok, put err
                    i.putExtra(DaemonActionNames.CONTENT, DaemonActionNames.CONTENT_ERR);
                    i.putExtra(DaemonActionNames.ERR_CODE, err.getContent().code);
                }

                broadcastIntent(i);
            }
        });
    }

    @Override
    public void waitForNewTurn() {
        clientDaemon.waitForNewTurn(new Runnable() {
            @Override
            public void run() {
                logger.d("New turn callback");
                AbstractReceivedMessage msg = clientDaemon.getResponseToLastAction();
                StartTurnReceivedMessage startTurn = ReceivedMessageTypeResolver.isStartTurn(msg);
                EndGameReceivedMessage endGame = ReceivedMessageTypeResolver.isEndGame(msg);
                ErrorReceivedMessage err = ReceivedMessageTypeResolver.isError(msg);

                Intent i = createIntent(DaemonActionNames.NEW_TURN_MESSAGE);
                if(startTurn != null) {
                    // new turn
                    i.putExtra(DaemonActionNames.CONTENT, startTurn);
                    i.putExtra(DaemonActionNames.ERR_CODE, ErrorCode.NO_ERROR);
                } else if (endGame != null){
                    // end game
                    i.putExtra(DaemonActionNames.CONTENT, endGame);
                    i.putExtra(DaemonActionNames.ERR_CODE, ErrorCode.NO_ERROR);
                } else {
                    i.putExtra(DaemonActionNames.CONTENT, DaemonActionNames.CONTENT_ERR);
                    i.putExtra(DaemonActionNames.ERR_CODE, err.getContent().code);
                }

                broadcastIntent(i);
            }
        });
    }

    @Override
    public void exit() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void stopDaemon() {
        clientDaemon.stopDaemon();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Creates a new intent with DAEMON_FILTER action and puts clientActionName as a CLIENT_ACTION_NAME string extra.
     * @param clientActionName
     * @return Newly created intent.
     */
    private Intent createIntent(String clientActionName) {
        Intent i = new Intent(DaemonActionNames.DAEMON_FILTER);
		i.putExtra(DaemonActionNames.ID, UUID.randomUUID());
        i.putExtra(DaemonActionNames.CLIENT_ACTION_NAME, clientActionName);
        return i;
    }

    /**
     * Broadcast intent usign local manager and saves the intent to queue.
     * @param intent
     */
    private void broadcastIntent(Intent intent) {
		intentQueue.add(intent);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        manager.sendBroadcast(intent);
    }
	
	/**
	 * Removes the intent from queue by its id.
	 */
	public void removeIntent(String intentId) {
		Iterator<Intent> itIt = intentQueue.iterator();
		while(itIt.hasNext()) {
			Intent it = itIt.next();
			if(it.getStringExtra(DaemonActionNames.ID).equals(intentId)) {
				itIt.remove();
				break;
			}
		}
	}
	
	/**
	 * Broadcasts the first intent in queue.
	 *
	 * @return Returns true if the queue wasn't empty and intent was broadcast. If the queue was empty, returns false.
	 */
	public boolean broadcastIntent() {
		if(intentQueue.isEmpty()) {
			return false;
		}
		
		Intent i = intentQueue.remove(0);
		broadcastIntent(i);
		return true;
	}
}
