package mkz.mkz_semestralka.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mkz.mkz_semestralka.R;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class EndGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
    }

    /**
     * Another server button callback.
     */
    public void onAnotherServerClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
