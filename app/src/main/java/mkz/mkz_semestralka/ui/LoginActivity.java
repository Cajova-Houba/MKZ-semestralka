package mkz.mkz_semestralka.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import mkz.mkz_semestralka.R;
import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.network.LoginData;
import mkz.mkz_semestralka.core.network.daemon.ClientDaemonService;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class LoginActivity extends AppCompatActivity {

    private final static Logger logger = Logger.getLogger(LoginActivity.class);

    private ClientDaemonService clientDaemonService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            logger.d("Assigning service object.");
            ClientDaemonService.LocalBinder localBinder =  (ClientDaemonService.LocalBinder)service;
            clientDaemonService = localBinder.getService();

            if(clientDaemonService != null){
                logger.d("Service is bonded successfully!");

                //do whatever you want to do after successful binding
            } else {
                logger.d("Service binding failed...");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logger.d("Creating LoginActivity.");
    }

    @Override
    protected void onStart() {
        super.onStart();
        logger.d("Binding client daemon service.");
        Intent intent = new Intent(this, ClientDaemonService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Connect button click callback.
     */
    public void onConnectClick(View view) {
//        Intent intent = new Intent(this, GameActivity.class);
        Handler handler = new Handler(Looper.getMainLooper());
        clientDaemonService.login(new LoginData("nick","localhost",65000),handler);
        //startActivity(intent);
    }
}

