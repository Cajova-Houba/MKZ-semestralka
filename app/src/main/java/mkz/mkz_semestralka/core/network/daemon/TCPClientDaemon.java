package mkz.mkz_semestralka.core.network.daemon;

import java.io.IOException;

import mkz.mkz_semestralka.core.Constrains;
import mkz.mkz_semestralka.core.error.Error;
import mkz.mkz_semestralka.core.error.ReceivingException;
import mkz.mkz_semestralka.core.message.received.AbstractReceivedMessage;
import mkz.mkz_semestralka.core.network.LoginData;
import mkz.mkz_semestralka.core.network.TcpClient;


/**
 * Daemon which will use tcp protocol to communicate with the server.
 *
 * @author Zdenek Vales
 */
// todo: use the tcp client
public class TCPClientDaemon extends ThreadClientDaemon {


    private TcpClient tcpClient;

    public TCPClientDaemon() {
        tcpClient = new TcpClient();
    }

    /**
     * The core of this mighty Daemon, the state machine which will fetch all logic.
     *
     * If the state is not one-line code, separate method which will handle everything should be used
     * for each state.
     *
     */
    @Override
    protected void stateMachine() {
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
                case WAIT_FOR_NEW_GAME:
                    waitForNewGameState();
                    break;
                case END_TURN:
                    endTurnState();
                    break;
                case WAIT_FOR_END_TURN_CONFIRM:
                    waitForEndTurnConfirmState();
                    break;
                case WAIT_FOR_NEW_TURN:
                    waitForNewTurnState();
                    break;
                case DISCONNECT:
                    disconnectState();
                    break;
            }
        }
    }

    private void disconnectState() {
        setResponseToLastAction(null);
        setCallback(null);
        try {
            tcpClient.disconnect();
        } catch (IOException e) {
            logger.w("Error while disconnecting the tcp client: "+e.getMessage());
        }

        setDaemonState(ClientDaemonState.IDLE);
    }



    /**
     * Daemon will send end turn message to the server.
     */
    private void endTurnState() {
        nullLastError();
        logger.d("End turn.");

        // get turn data
        int[] p1TurnWord = new int[Constrains.MAX_NUMBER_OF_STONES];
        int[] p2TurnWord = new int[Constrains.MAX_NUMBER_OF_STONES];
        int[] data = (int[])getData();
        for(int i = 0; i < Constrains.MAX_NUMBER_OF_STONES; i++) {
            p1TurnWord[i] = data[i];
            p2TurnWord[i] = data[i+Constrains.MAX_NUMBER_OF_STONES];
        }

        // send end turn message
        try {
            tcpClient.sendEndTurnMessage(p1TurnWord, p2TurnWord);
        } catch (IOException e) {
            logger.e("Error while sending end turn message: "+e.getMessage());
            setLastError(Error.NO_CONNECTION());

            // call callback
            callback.run();

            // switch to idle state
            setDaemonState(ClientDaemonState.IDLE);
            return;
        }

        setDaemonState(ClientDaemonState.WAIT_FOR_END_TURN_CONFIRM);
    }

    private void waitForEndTurnConfirmState() {
        logger.d("Waiting for end turn confirm.");

        // receive message
        try {
            AbstractReceivedMessage msg = tcpClient.receiveMessage();
            setResponseToLastAction(msg);
        } catch (IOException e) {
            logger.e("Error while sending login message: "+e.getMessage());
            setLastError(Error.NO_CONNECTION());
        } catch (ReceivingException e) {
            logger.e("Error while sending login message: "+e.getMessage());
            setLastError(e.error);
        }

        // call callback
        getCallback().run();

        setDaemonState(ClientDaemonState.IDLE);
    }

    /**
     * Daemon is waiting for a new turn message from server.
     */
    private void waitForNewTurnState() {
        logger.d("Waiting for new turn.");

        // receive message
        try {
            AbstractReceivedMessage msg = tcpClient.receiveMessage();
            setResponseToLastAction(msg);
        } catch (IOException e) {
            logger.e("Error while sending login message: "+e.getMessage());
            setLastError(Error.NO_CONNECTION());
        } catch (ReceivingException e) {
            logger.e("Error while sending login message: "+e.getMessage());
            setLastError(e.error);
        }

        // callback
        getCallback().run();

        setDaemonState(ClientDaemonState.IDLE);
    }

    /**
     * Daemon is waiting for new game message.
     */
    private void waitForNewGameState() {
        nullLastError();
        logger.d("Waiting for new game.");

        // receive messages
        try {
            AbstractReceivedMessage msg = tcpClient.receiveMessage();
            setResponseToLastAction(msg);
        } catch (IOException e) {
            logger.e("Error while sending login message: "+e.getMessage());
            setLastError(Error.NO_CONNECTION());
        } catch (ReceivingException e) {
            logger.e("Error while sending login message: "+e.getMessage());
            setLastError(e.error);
        }

        // call callback
        getCallback().run();

        setDaemonState(ClientDaemonState.IDLE);
    }

    /**
     * Waits for the response and then calls the callback using main handler.
     */
    private void loginResponseWaitState() {
        logger.d("Waiting for login response.");

        // receive login response
        try {
            AbstractReceivedMessage msg = tcpClient.receiveMessage();
            setResponseToLastAction(msg);
        } catch (IOException e) {
            logger.e("Error while sending login message: "+e.getMessage());
            setLastError(Error.NO_CONNECTION());
        } catch (ReceivingException e) {
            logger.e("Error while sending login message: "+e.getMessage());
            setLastError(e.error);
        }

        // call the callback
        getCallback().run();

        // switch to idle state
        setDaemonState(ClientDaemonState.IDLE);
    }

    /**
     * Daemon will send the login data and then waits for the response.
     */
    private void loginState() {
        nullLastError();
        LoginData loginData = (LoginData) getData();
        logger.d("Logging to "+loginData.toString());

        // send login
        try {
            tcpClient.sendLoginMessage(loginData);
        } catch (IOException e) {
            logger.e("Error while sending login message: "+e.getMessage());
            setLastError(Error.NO_CONNECTION());

            // call callback
            callback.run();

            // switch to idle state
            setDaemonState(ClientDaemonState.IDLE);
            return;
        }

        // sending ok -> switch to waiting state
        setDaemonState(ClientDaemonState.LOGIN_RESPONSE_WAIT);
    }
}
