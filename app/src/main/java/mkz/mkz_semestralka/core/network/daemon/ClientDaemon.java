package mkz.mkz_semestralka.core.network.daemon;


import android.os.Handler;

import mkz.mkz_semestralka.core.message.received.AbstractReceivedMessage;
import mkz.mkz_semestralka.core.network.LoginData;

/**
 * A daemon thread which will fetch communication with the server.
 * The main thread will work only with UI, everything else should be done by daemon thread.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */

public class ClientDaemon extends Thread implements Daemon {

    private ClientDaemonState state;

    /**
     * Response to the last manually message sent to server. Responses to ALIVE message are
     * not stored here. Responses to LOGIN message, END_TURN message are stored here.
     */
    private AbstractReceivedMessage responseToLastAction;

    /**
     * Callback to be passed to mainHandler.
     */
    private Runnable callback;

    /**
     * Data to be passed from methods. Type depends on the current state, so this should be handled
     * carefully.
     */
    private Object data;

    public ClientDaemon() {
        state = ClientDaemonState.IDLE;
    }

    @Override
    public void run() {
        super.run();

        // call the state machine which will do all the dirty work
        stateMachine();
    }

    @Override
    public synchronized AbstractReceivedMessage getResponseToLastAction() {
        return responseToLastAction;
    }

    @Override
    public synchronized void login(LoginData loginData, Runnable callback) {
        this.state = ClientDaemonState.LOGIN;
        this.data = loginData;
        this.callback = callback;
    }

    @Override
    public synchronized void exit() {
    }

    @Override
    public synchronized void stopDaemon() {
        state = ClientDaemonState.STOP;
    }

    /**
     * The core of this mighty Daemon, the state machine which will fetch all logic.
     *
     * If the state is not one-line code, separate method which will handle everything should be used
     * for each state.
     *
     */
    private void stateMachine() {
        while(state != ClientDaemonState.STOP) {
            switch (state) {
                case IDLE:
                    // do nothing
                    break;
                case LOGIN:
                    loginState();
                    break;
                case LOGIN_RESPONSE_WAIT:
                    loginResponseWaitState();
                    break;
            }
        }
    }


    /**
     * Waits for the response and then calls the callback using main handler.
     */
    // todo: wait for login response
    private void loginResponseWaitState() {
        // wait for response

        // call the callback
        callback.run();

        // switch to idle state
        state = ClientDaemonState.IDLE;
    }

    /**
     * Daemon will send the login data and then waits for the response.
     */
    // todo: handle login state
    private void loginState() {
        // send login

        // switch the state
        state = ClientDaemonState.LOGIN_RESPONSE_WAIT;
    }


}
