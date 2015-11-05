package es.floppysoftware.robots;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Splash Screen.
 *
 * (c) 2015 Miguel I. Garcia Lopez / FloppySoftware.
 *
 * @version 1.00
 * @since   05 Nov 2015
 *
 * www.floppysoftware.es
 * floppysoftware@gmail.com
 */
public class SplashActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Timer to execute the main activity after 3 seconds
        CountDownTimer timer = new CountDownTimer(3000, 500) {
            @Override
            public void onTick(long l) {
                // Not used
            }

            @Override
            public void onFinish() {
                // Create intent
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                // Start main activity
                startActivity(intent);

                // Finish this activity
                finish();
            }
        }.start();
    }
}
