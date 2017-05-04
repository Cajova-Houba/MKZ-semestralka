package mkz.mkz_semestralka.core.network.daemon;

import mkz.mkz_semestralka.core.Constrains;
import mkz.mkz_semestralka.core.message.received.OkReceivedMessage;
import mkz.mkz_semestralka.core.message.received.StartGameReceivedMessage;
import mkz.mkz_semestralka.core.message.received.StartTurnReceivedMessage;

/**
 * Daemon which will return mock data. Used for testing.
 *
 * @author Zdenek Vales
 */

public class MockClientDaemon extends ThreadClientDaemon {

    // used to hold last sent turn word and return it on next turn
    private int[] firstPlayerPos;
    private int[] secondPlayerPos;

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
            }
        }
    }


    private void waitForEndTurnConfirmState() {
        logger.d("Waiting for end turn confirm.");

        // receive message
        setResponseToLastAction(new OkReceivedMessage());

        // call callback
        callback.run();

        setDaemonState(ClientDaemonState.IDLE);
    }

    /**
     * Daemon will send end turn message to the server.
     */
    private void endTurnState() {
        logger.d("End turn.");

        int[] tmp = (int[])getData();
        if( firstPlayerPos == null) {
            firstPlayerPos = new int[Constrains.MAX_NUMBER_OF_STONES];
        }
        if(secondPlayerPos == null) {
            secondPlayerPos = new int[Constrains.MAX_NUMBER_OF_STONES];
        }

        for (int i = 0; i < Constrains.MAX_NUMBER_OF_STONES; i++) {
            firstPlayerPos[i] = tmp[i];
        }

        for (int i = Constrains.MAX_NUMBER_OF_STONES; i < 2*Constrains.MAX_NUMBER_OF_STONES; i++) {
            secondPlayerPos[i-Constrains.MAX_NUMBER_OF_STONES] = tmp[i];
        }

        // send end turn message

        setDaemonState(ClientDaemonState.WAIT_FOR_END_TURN_CONFIRM);
    }

    /**
     * Daemon is waiting for a new turn message from server.
     */
    private void waitForNewTurnState() {
        logger.d("Waiting for new turn.");

        // receive message
        setResponseToLastAction(new StartTurnReceivedMessage(firstPlayerPos, secondPlayerPos));

        getCallback().run();

        setDaemonState(ClientDaemonState.IDLE);
    }

    /**
     * Daemon is waiting for new game message.
     */
    // todo: wait for new game
    private void waitForNewGameState() {
        logger.d("Waiting for new game.");

        // receive messages
        setResponseToLastAction(new StartGameReceivedMessage("valesz", "p-2"));

        // call callback
        getCallback().run();

        setDaemonState(ClientDaemonState.IDLE);
    }

    /**
     * Waits for the response and then calls the callback using main handler.
     */
    private void loginResponseWaitState() {
        logger.d("Waiting for login response.");

        // call the callback
        getCallback().run();

        // switch to idle state
        setDaemonState(ClientDaemonState.IDLE);
    }

    /**
     * Daemon will send the login data and then waits for the response.
     */
    private void loginState() {
        // send login
        logger.d("Logging to "+getData().toString());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.d("Logged.");

        // set the received message
        setResponseToLastAction(new OkReceivedMessage());
//        Random r = new Random();
//        int i = r.nextInt(8)+43;
//        if(i > 46) {
//            responseToLastAction = new OkReceivedMessage();
//        } else {
//            responseToLastAction = new ErrorReceivedMessage(ErrorCode.getCodeByInt(i));
//        }

        // switch the state
        setDaemonState(ClientDaemonState.LOGIN_RESPONSE_WAIT);
    }
}
