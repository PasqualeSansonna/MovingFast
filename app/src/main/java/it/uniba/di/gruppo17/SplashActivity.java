package it.uniba.di.gruppo17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author Pasquale
 */
public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_LAYOUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent  MainActivityIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(MainActivityIntent);
                finish();
            }
        }, SPLASH_TIME_LAYOUT);
}
}
