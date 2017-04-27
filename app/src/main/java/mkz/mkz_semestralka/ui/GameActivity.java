package mkz.mkz_semestralka.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import mkz.mkz_semestralka.R;
import mkz.mkz_semestralka.controller.Controller;
import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.game.Game;
import mkz.mkz_semestralka.core.network.daemon.ClientDaemonService;
import mkz.mkz_semestralka.core.network.daemon.DaemonActionNames;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class GameActivity extends AppCompatActivity {

    private final static Logger logger = Logger.getLogger(GameActivity.class);

    public static final String DEF_P1_NICK = "-";
    public static final String DEF_P2_NICK = "-";
    public static final String DEF_THROW = "-";

    private Controller controller;

    private ClientDaemonService clientDaemonService = ClientDaemonService.getInstance();

    private LocalBroadcastManager broadcastManager;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // todo: handle broadcasts
            logger.d("Broadcast received! "+intent.getAction());
            if(intent.getStringExtra(DaemonActionNames.CLIENT_ACTION_NAME).equals(DaemonActionNames.START_GAME_RESPONSE)) {
                logger.d("Start game response received from daemon!");
                controller.handleStartNewGame(intent);
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
        Intent intent = new Intent(this, ClientDaemonService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(DaemonActionNames.DAEMON_FILTER));
        controller = Controller.getInstance();
        controller.setGameActivity(this);

        switch (Game.getInstance().getState()) {
            case WAITING_FOR_OPPONENT:
                clientDaemonService.waitForStartGame();
                displayP1Nick(controller.getTmpPlayerName());
                displayP2Nick(DEF_P2_NICK);
                break;
            case RUNNING:
                displayP1Nick(Game.getInstance().getFirstPlayer().getNick());
                displayP2Nick(Game.getInstance().getSecondPlayer().getNick());
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unbindService(mConnection);
        unregisterReceiver(broadcastReceiver);
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
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void startGame() {
        // todo: display stuff, timer...
        displayP1Nick(Game.getInstance().getFirstPlayer().getNick());
        displayP2Nick(Game.getInstance().getSecondPlayer().getNick());
    }

    public void onThrowClick(View view) {
        controller.throwSticks();
    }
}
