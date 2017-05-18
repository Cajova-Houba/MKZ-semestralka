package mkz.mkz_semestralka.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import mkz.mkz_semestralka.R;
import mkz.mkz_semestralka.controller.Controller;
import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.network.LoginData;
import mkz.mkz_semestralka.core.network.daemon.ClientDaemonService;
import mkz.mkz_semestralka.core.network.daemon.DaemonActionNames;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class LoginActivity extends AppCompatActivity {

    private final static Logger logger = Logger.getLogger(LoginActivity.class);

    private ClientDaemonService clientDaemonService = ClientDaemonService.getInstance();

    private LoginData loginData;

    private Controller controller;

    private LocalBroadcastManager broadcastManager;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            logger.d("Broadcast received! "+intent.getAction());
            if(intent.getStringExtra(DaemonActionNames.CLIENT_ACTION_NAME).equals(DaemonActionNames.LOGIN_RESPONSE)) {
                logger.d("Login response received from daemon!");
                controller.handleLogin(intent, loginData);
            } else {
                logger.w("Unsupported daemon action: "+intent.getStringExtra(DaemonActionNames.CLIENT_ACTION_NAME));
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        logger.d("Resuming LoginActivity.");
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(DaemonActionNames.DAEMON_FILTER));
        controller = Controller.getInstance();
        controller.setLoginActivity(this);

        // todo: check if is already connected
        controller.checkIsLogged();
		
		
		// this loop will broadcast all intents left in the queue
		while(clientDaemonService.broadcastIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.d("Creating LoginActivity.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Connect button click callback.
     */
    public void onConnectClick(View view) {
        enableConnectBtn(false);
        String nick = ((EditText)findViewById(R.id.nickEdit)).getText().toString();
        String addr = ((EditText)findViewById(R.id.addressEdit)).getText().toString();
        String portStr = ((EditText)findViewById(R.id.portEdit)).getText().toString();
        try {
            // todo: validation
            int port = Integer.parseInt(portStr);
            loginData = new LoginData(nick, "147.228.134.129", port);
            clientDaemonService.login(loginData);
        } catch (NumberFormatException e) {
            logger.e("Exception while converting the port number: "+e.getMessage());
            displayErrorMessage("Špatné číslo portu.");
        }
    }

    public void enableConnectBtn(boolean enable) {
        findViewById(R.id.connectBtn).setEnabled(enable);
    }

    public void displayErrorMessage(String errorMessage) {
        ((TextView)findViewById(R.id.errorText)).setText(errorMessage);
    }

    public void displayMainView() {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }
}

