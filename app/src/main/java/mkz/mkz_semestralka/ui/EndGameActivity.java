package mkz.mkz_semestralka.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import mkz.mkz_semestralka.R;
import mkz.mkz_semestralka.core.Logger;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class EndGameActivity extends AppCompatActivity {

    private final static Logger logger = Logger.getLogger(EndGameActivity.class);

    /**
     * Pass an extra field in intent with this name to display winner's name.
     */
    public static final String WINNERS_NAME_FIELD = "WINNERS_NAME";

    public static final String END_GAME_MESSAGE_FORMAT = "The game has ended, %s won!";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        // get winner's name
        Intent i = getIntent();
        String winner = i.getStringExtra(WINNERS_NAME_FIELD);
        if(winner != null) {
            ((TextView)findViewById(R.id.winnerText)).setText(String.format(END_GAME_MESSAGE_FORMAT, winner));
        } else {
            logger.w("No winner!");
            displayLoginActivity();
        }
    }

    /**
     * Another server button callback.
     */
    public void onAnotherServerClick(View view) {
        displayLoginActivity();
    }

    private void displayLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
