package mkz.mkz_semestralka.core.network.daemon;


import mkz.mkz_semestralka.core.message.received.OkReceivedMessage;
import mkz.mkz_semestralka.core.message.received.StartGameReceivedMessage;

/**
 * A daemon thread which will fetch communication with the server.
 * The main thread will work only with UI, everything else should be done by daemon thread.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */

public class ClientDaemon extends ThreadClientDaemon implements Daemon {


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
                    break;
                case WAIT_FOR_NEW_TURN:
                    break;
            }
        }
    }


    /**
     * Daemon will send end turn message to the server.
     */
    // todo: end turn
    private void endTurnState() {

        state = ClientDaemonState.WAIT_FOR_NEW_TURN;
    }

    /**
     * Daemon is waiting for a new turn message from server.
     */
    // todo: wait for new turn
    private void waitForNewTurnState() {
        state = ClientDaemonState.IDLE;
    }

    /**
     * Daemon is waiting for new game message.
     */
    // todo: wait for new game
    private void waitForNewGameState() {
        logger.d("Waiting for new game.");

        // receive messages
        responseToLastAction = new StartGameReceivedMessage("valesz", "p-2");
        callback.run();

        state = ClientDaemonState.IDLE;
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
        logger.d("Logging to "+data.toString());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.d("Logged.");

        // set the received message
        responseToLastAction = new OkReceivedMessage();
//        Random r = new Random();
//        int i = r.nextInt(8)+43;
//        if(i > 46) {
//            responseToLastAction = new OkReceivedMessage();
//        } else {
//            responseToLastAction = new ErrorReceivedMessage(ErrorCode.getCodeByInt(i));
//        }

        // switch the state
        state = ClientDaemonState.LOGIN_RESPONSE_WAIT;
    }


}
