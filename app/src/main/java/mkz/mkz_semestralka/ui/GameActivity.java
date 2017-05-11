package mkz.mkz_semestralka.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mkz.mkz_semestralka.R;
import mkz.mkz_semestralka.controller.Controller;
import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.game.Game;
import mkz.mkz_semestralka.core.network.daemon.ClientDaemonService;
import mkz.mkz_semestralka.core.network.daemon.DaemonActionNames;
import mkz.mkz_semestralka.ui.components.BoardView;
import mkz.mkz_semestralka.ui.components.Stone;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class GameActivity extends AppCompatActivity {

    private final static Logger logger = Logger.getLogger(GameActivity.class);

    /**
     * For saving last thrown value.
     */
    private static final String LAST_THROWN_VAL = "LAST_THROWN_VAL";

    public static final String DEF_P2_NICK = "-";

    private Controller controller;

    private ClientDaemonService clientDaemonService = ClientDaemonService.getInstance();

    private LocalBroadcastManager broadcastManager;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // todo: handle broadcasts
            logger.d("Broadcast received! "+intent.getAction());
            String actionName = intent.getStringExtra(DaemonActionNames.CLIENT_ACTION_NAME);
            if(actionName.equals(DaemonActionNames.START_GAME_RESPONSE)) {
                logger.d("Start game response received from daemon!");
                controller.handleStartNewGame(intent);
            } else if (actionName.equals(DaemonActionNames.END_TURN_RESPONSE)) {
                logger.d("End turn response received from daemon!");
                controller.handleEndTurnResponse(intent, clientDaemonService);
            } else if (actionName.equals(DaemonActionNames.NEW_TURN_MESSAGE)) {
                logger.d("New turn message received from daemon!");
                controller.handleNewTurn(intent);
            } else {
                logger.w("Unsupported daemon action: "+intent.getStringExtra(DaemonActionNames.CLIENT_ACTION_NAME));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logger.d("Binding client daemon service.");
        ((BoardView)findViewById(R.id.boardView)).setParent(this);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(DaemonActionNames.DAEMON_FILTER));
        controller = Controller.getInstance();
        controller.setGameActivity(this);

        switch (Game.getInstance().getState()) {
            case WAITING_FOR_OPPONENT:
                logger.d("Waiting for opponent.");
                clientDaemonService.waitForStartGame();
                displayP1Nick(controller.getTmpPlayerName());
                displayP2Nick(DEF_P2_NICK);
                break;
            case RUNNING:
                logger.d("The game is already running.");
                displayP1Nick(Game.getInstance().getFirstPlayer().getNick());
                displayP2Nick(Game.getInstance().getSecondPlayer().getNick());
                updateStones(Game.getInstance().getFirstPlayer().getStones(), Game.getInstance().getSecondPlayer().getStones());
                setLastThrownVal(Game.getInstance().getThrownValue());
                resumeButtons();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            broadcastManager.unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException ex) {
            // go fuck yourself android
        }
    }

    /**
     * Sets correct enabled value to buttons.
     */
    private void resumeButtons() {
        if(!Game.getInstance().isMyTurn()) {
            disableButtons();
        } else if(Game.getInstance().alreadyThrown()) {
            enableThrowButton(false);
        }
    }

    private void setLastThrownVal(int lastThrownVal) {
        if(Game.getInstance().isMyTurn() && lastThrownVal > 0) {
            logger.d("My turn!");
            setThrowText(String.valueOf(lastThrownVal));
        } else {
            logger.d("Not my turn!");
            setThrowText(getString(R.string.game_throw_value));
        }
    }

    private void displayP1Nick(String p1Nick) {
        ((TextView)findViewById(R.id.player1Text)).setText(p1Nick);
    }

    private void displayP2Nick(String p2Nick) {
        ((TextView)findViewById(R.id.player2Text)).setText(p2Nick);
    }

    public void setThrowText(String throwText) {
        ((TextView)findViewById(R.id.throwText)).setText(throwText);
    }

    public void enableThrowButton(boolean enable) {
        findViewById(R.id.throwBtn).setEnabled(enable);
    }

    public void enableEndTurnButton(boolean enable) {
        findViewById(R.id.endTurnBtn).setEnabled(enable);
    }

    public void displayToast(String message) {
//        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
//        toast.show();
        ((TextView)findViewById(R.id.messageTextView)).setText(message);
    }

    public void disableButtons() {
        enableEndTurnButton(false);
        enableThrowButton(false);
    }

    public void enableButtons() {
        enableEndTurnButton(true);
        enableThrowButton(true);
    }

    public void startGame() {
        // todo: display stuff, timer...
        displayP1Nick(Game.getInstance().getFirstPlayer().getNick());
        displayP2Nick(Game.getInstance().getSecondPlayer().getNick());
    }

    public void updateStones(int[] firsPlayerPositions, int[] seconPlayerPositions) {
        BoardView boardView = (BoardView) findViewById(R.id.boardView);
        boardView.updateStones(firsPlayerPositions, seconPlayerPositions);
        boardView.invalidate();
    }

    /**
     * Prepares components on the mane pane (not on the board!) for the
     * new turn.
     */
    public void newTurn() {
        enableButtons();
        setThrowText("-");
        displayToast("Nový tah.");
    }

    public void resetTimerText() {
        ((TextView)findViewById(R.id.turnTime)).setText("--:--");
    }

    /**
     * Sets time text to remaining time.
     * @param remainingTime Remaining time in seconds.
     */
    public void updateTimerText(int remainingTime) {
        int time = remainingTime;
        if(time < 0) {
            time = 0;
        }
        int minutes = time / 60;
        time = time - minutes*60;
        ((TextView)findViewById(R.id.turnTime)).setText(String.format("%d:%d", minutes, time));
    }

    public void onThrowClick(View view) {
        controller.throwSticks();
    }

    public void onEndTurnClick(View view) {
        controller.endTurn(clientDaemonService);
    }

    public ClientDaemonService getClientDaemonService() {
        return clientDaemonService;
    }

    /**
     * Returns the stone selected on a BoardView.
     * @return
     */
    public Stone getSelected() {
        BoardView boardView = (BoardView) findViewById(R.id.boardView);
        return boardView.getSelected();
    }

    /**
     * Deselect stone selected on board view.
     */
    public void deselect() {
        BoardView boardView = (BoardView) findViewById(R.id.boardView);
        boardView.deselect();
    }

    public void showLeaveButton() {
        Button leaveButton = (Button) findViewById(R.id.leaveBtn);
        leaveButton.setEnabled(true);
        leaveButton.setVisibility(View.VISIBLE);
    }

    public void hideLeaveButton() {
        Button leaveButton = (Button) findViewById(R.id.leaveBtn);
        leaveButton.setEnabled(false);
        leaveButton.setVisibility(View.INVISIBLE);
    }

    public void leaveButtonClick(View view) {
        controller.leaveBoard();
    }

    /**
     * Switches to end game activity and displays winner's name.
     * @param winner
     */
    public void displayEndGame(String winner) {
        Intent i = new Intent(this, EndGameActivity.class);
        // todo: move WINNER_NAME to constant
        i.putExtra(EndGameActivity.WINNERS_NAME_FIELD, winner);
        startActivity(i);
    }
}
