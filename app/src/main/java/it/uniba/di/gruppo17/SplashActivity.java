package it.uniba.di.gruppo17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * @author Pasquale rivisited by Andrea ;)
 * Activity di caricamento iniziale
 */
public class SplashActivity extends AppCompatActivity {

    private ImageView image = null;
    private Animation animation = null;
    private static int SPLASH_TIME_LAYOUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        image = (ImageView) findViewById(R.id.logo);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_scale);
        avvia();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent  MainActivityIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(MainActivityIntent);
                finish();
            }
        }, SPLASH_TIME_LAYOUT);
}
    public void avvia(){
        image.startAnimation(animation);
    }
}
