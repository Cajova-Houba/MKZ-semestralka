package mkz.mkz_semestralka.core.network.daemon;

import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.error.Error;
import mkz.mkz_semestralka.core.message.received.AbstractReceivedMessage;
import mkz.mkz_semestralka.core.network.LoginData;

/**
 * Created on 04.05.2017.
 *
 * @author Zdenek Vales
 */

public abstract class ThreadClientDaemon extends Thread implements Daemon{

    protected final static Logger logger = Logger.getLogger(ClientDaemon.class);

    protected ClientDaemonState state;

    protected Error lastError;

    /**
     * Response to the last manually message sent to server. Responses to ALIVE message are
     * not stored here. Responses to LOGIN message, END_TURN message are stored here.
     */
    protected AbstractReceivedMessage responseToLastAction;

    /**
     * Callback to be passed to mainHandler.
     */
    protected Runnable callback;

    /**
     * Data to be passed from methods. Type depends on the current state, so this should be handled
     * carefully.
     */
    protected Object data;

    public ThreadClientDaemon() {
        state = ClientDaemonState.IDLE;
    }

    @Override
    public void run() {
        logger.d("Starting the client daemon.");

        // call the state machine which will do all the dirty work
        stateMachine();
    }

    public synchronized void setDaemonState(ClientDaemonState state) {
        logger.d("Setting state to: "+state);
        this.state = state;
    }

    public synchronized ClientDaemonState getDaemonState() {
        return this.state;
    }

    public synchronized Runnable getCallback() {
        return callback;
    }

    public synchronized void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public synchronized Object getData() {
        return data;
    }

    public synchronized void setData(Object data) {
        this.data = data;
    }

    @Override
    public synchronized Error getLastError() {
        return lastError;
    }

    public synchronized  void setLastError(Error lastError) {
        this.lastError = lastError;
    }

    @Override
    public synchronized void nullLastError() {
        this.lastError = null;
    }

    @Override
    public synchronized AbstractReceivedMessage getResponseToLastAction() {
        return responseToLastAction;
    }

    public synchronized void setResponseToLastAction(AbstractReceivedMessage responseToLastAction) {
        this.responseToLastAction = responseToLastAction;
    }

    @Override
    public void login(LoginData loginData, Runnable callback) {
        setData(loginData);
        setCallback(callback);
        setDaemonState(ClientDaemonState.LOGIN);
    }

    @Override
    public void exit() {
    }

    @Override
    public void stopDaemon() {
        state = ClientDaemonState.STOP;
    }

    @Override
    public void waitForStartGame(Runnable callback) {
        setCallback(callback);
        setDaemonState(ClientDaemonState.WAIT_FOR_NEW_GAME);
    }

    @Override
    public void disconnect() {
        setDaemonState(ClientDaemonState.DISCONNECT);
    }

    @Override
    public void endTurn(int[] firstPlayerStones, int[] secondPlayerStones, Runnable callback) {
        // convert both arrays to one
        int tmp[] = new int[firstPlayerStones.length+secondPlayerStones.length];
        for(int i = 0; i < firstPlayerStones.length; i++) {
            tmp[i] = firstPlayerStones[i];
        }
        for(int i = 0; i < secondPlayerStones.length; i++) {
            tmp[i+firstPlayerStones.length] = secondPlayerStones[i];
        }
        setData(tmp);
        setCallback(callback);
        setDaemonState(ClientDaemonState.END_TURN);
    }

    @Override
    public void waitForNewTurn(Runnable callback) {
        setCallback(callback);
        setDaemonState(ClientDaemonState.WAIT_FOR_NEW_TURN);
    }

    protected abstract void stateMachine();

}
