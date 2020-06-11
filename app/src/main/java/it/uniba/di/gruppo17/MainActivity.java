package it.uniba.di.gruppo17;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;

import it.uniba.di.gruppo17.services.LocationService;
import it.uniba.di.gruppo17.util.Keys;

import static it.uniba.di.gruppo17.util.Keys.REQUEST_RESOLVE_ERROR;
import static it.uniba.di.gruppo17.util.Keys.RESOLVING_ERROR_STATE_KEY;
import static it.uniba.di.gruppo17.util.Keys.REQUEST_ACCESS_LOCATION;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout drawer;
    private NavigationView mNavingationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences prefs;

    //Per controllo Google play services
    private boolean mResolvingError;
    private GoogleApiClient mApiClient;

    // Per layout bottoni
    private ImageView IV_searchScooter;
    private ImageView IV_profile;
    private ImageView IV_wallet;

    ImageView image;
    ImageView image2 = null;
    LinearLayout linearLayout = null;
    Animation logo_anim = null;
    Animation bg_anim;
    Animation fromBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Poiché non usiamo ActionBar, usiamo Toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Impostazione del navigation Drawer sulla sinistra
        drawer = findViewById(R.id.drawer_layout);
        drawer.setElevation(16);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState(); /*For hamburger */

        //Per passare ai fragment delle voci nel menu laterale
        mNavingationView = findViewById(R.id.nav_view);
        mNavingationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                openFragment(menuItem);
                drawer.closeDrawer(mNavingationView);
                return false;
            }
        });

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this) //Permette di ricevere notifiche relativa alla connessione/disconessione dei Google Play services
                .addOnConnectionFailedListener(this)//permette di gestire errori legati al ciclo di vita dell'oggetto mApiClient
                .build();

        if ( savedInstanceState!= null )
            mResolvingError = savedInstanceState.getBoolean(RESOLVING_ERROR_STATE_KEY, false);


        IV_searchScooter = (ImageView) findViewById(R.id.IV_SearchScooter);
        IV_profile = (ImageView) findViewById(R.id.IV_profile);
        IV_wallet = (ImageView) findViewById(R.id.IV_wallet);
        image = (ImageView) findViewById(R.id.imageBG);
        image2 = (ImageView) findViewById(R.id.imageLogo);
        linearLayout = (LinearLayout) findViewById(R.id.LinearLayoutButton);

        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        logo_anim = AnimationUtils.loadAnimation(this, R.anim.logo_scale);
        bg_anim = AnimationUtils.loadAnimation(this, R.anim.bg_home);
        fromBottom.setDuration(1000);
        linearLayout.startAnimation(fromBottom);
        Display display = getWindowManager().getDefaultDisplay();
        // display size in pixels
        Point size = new Point();
        display.getSize(size);
        Integer height = size.y;
        image.animate().translationY((float) (-height*4.7/10)).setDuration(1000);
        logo_anim.setDuration(1000);
        image2.startAnimation(logo_anim);

        IV_searchScooter.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if ( getSupportFragmentManager().findFragmentByTag("MapsFragment") == null ){
                    Fragment nextFragment = new MapsFragment();
                    getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,nextFragment).commit();
                }
                else
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, getSupportFragmentManager().findFragmentByTag("MapsFragment")).commit();

            }
        });

        IV_profile.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if ( getSupportFragmentManager().findFragmentByTag("ProfileFragment") == null ){
                    Fragment nextFragment = new ProfileFragment();
                    getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,nextFragment).commit();
                }
                else
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, getSupportFragmentManager().findFragmentByTag("ProfileFragment")).commit();

            }
        });


        IV_wallet.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if ( getSupportFragmentManager().findFragmentByTag("WalletFragment") == null ){
                    Fragment nextFragment = new WalletFragment();
                    getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,nextFragment).commit();
                }
                else
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, getSupportFragmentManager().findFragmentByTag("WalletFragment")).commit();

            }
        });
    }

    /**
     * Metodo che crea il secondo menu (laterale)
     * @param menu
     * @return true creazione menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return true;
    }


    /**
     * Metodo che gestisce la selezione degli item del menu dx di impostazioni
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logout_fragment:
                logout();
                break;
            default:
                break;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    /**
     * Metodo che effettua logout
     * Si cancellano i dati dell'utente loggato nelle Shared Preferences e si avvia intent su Login
     */
    private void logout() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.logout_title)
                .setMessage(R.string.logout_messageDialog)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prefs = getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove(Keys.EMAIL);
                        editor.remove(Keys.PASSWORD);
                        editor.remove(Keys.USER_ID);
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .create().show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() < 1 )
                this.finish();
            else
                getSupportFragmentManager().popBackStack();
            //super.onBackPressed();
        }
    }

    /**
     * Metodo per passare dalla main activity ai fragment delle voci nel menu laterale
     * @author francesco moramarco
     */
    private void openFragment(MenuItem menuItem)
    {
        prefs = getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int itemId = menuItem.getItemId();
        Fragment nextFragment = null;
        switch (itemId)
        {
            case R.id.nav_home:
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                break;
            case R.id.nav_map:
                getSupportFragmentManager().popBackStack();
                if ( getSupportFragmentManager().findFragmentByTag("MapsFragment") == null )
                {
                    nextFragment = new MapsFragment();
                    getSupportFragmentManager().beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container, nextFragment,"MapsFragment")
                            .commit();
                }
                else
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, getSupportFragmentManager().findFragmentByTag("MapsFragment"))
                            .commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().popBackStack();
                if ( getSupportFragmentManager().findFragmentByTag("ProfileFragment") == null )
                {
                    nextFragment = new ProfileFragment();
                    getSupportFragmentManager().beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container, nextFragment,"ProfileFragment")
                            .commit();
                }
                else
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, getSupportFragmentManager().findFragmentByTag("ProfileFragment"))
                            .commit();
                break;
            case R.id.nav_rent:
                getSupportFragmentManager().popBackStack();
                //Se l'utente ha noleggiato
                if ( prefs.getBoolean(Keys.IN_RENT,false) && prefs.getInt(Keys.RENT_ID,-1) != -1 )
                {
                    nextFragment = new ResultFragment();
                    getSupportFragmentManager().beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container,nextFragment).commit();
                    break;
                }
                //Nessun noleggio
                if ( getSupportFragmentManager().findFragmentByTag("RentFragment") == null ){
                    nextFragment = new RentFragment();
                    getSupportFragmentManager().beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container,nextFragment,"RentFragment")
                            .commit();
                }
                else
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,getSupportFragmentManager().findFragmentByTag("RentFragment"))
                            .commit();
                break;
            case R.id.nav_wallet:
                if ( getSupportFragmentManager().findFragmentByTag("WalletFragment") == null ){
                    nextFragment = new WalletFragment();
                    getSupportFragmentManager().beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container,nextFragment,"WalletFragment")
                            .commit();
                }
                else
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,getSupportFragmentManager().findFragmentByTag("WalletFragment"))
                            .commit();
                break;
            case R.id.nav_reports:
                getSupportFragmentManager().popBackStack();
              /*  if ( getSupportFragmentManager().findFragmentByTag("ReportFragment") == null ){
                    nextFragment = new ReportProblemsFragment();
                    getSupportFragmentManager().beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container,nextFragment,"ReportFragment")
                            .commit();
                }
                else
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,getSupportFragmentManager().findFragmentByTag("ReportFragment"))
                            .commit();*/
                Intent reportActivity = new Intent(this, ReportActivity.class);
                startActivity(reportActivity);
                break;
            default:
                throw new IllegalArgumentException("No fragment for the given item");
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //Per non entrare in un loop
        if(!mResolvingError)
            mApiClient.connect();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(!mResolvingError)
            mApiClient.connect();
    }
    @Override
    protected void onStop()
    {
        mApiClient.disconnect();
        super.onStop();
    }


    /**
     * @author Francesco Moramarco
     * Se l'invocazione dei Google Play services è andata a buon fine, viene chiamato il seguento metodo
     * Per cui controlliamo ora se i permessi di localizzazione e se la geolocalizzazione del dispositivo sono attivi
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        checkLocationPermission();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Override del metodo onConncetionFailde per gestire problemi relativi ai Google Play services
     * @author Francesco moramarco
     * @param connectionResult - contiene infomrazioni relative all'errore e gli strumenti per la sua risoluzione
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if ( mResolvingError )
            return;
        else if ( connectionResult.hasResolution() )
        {
            try{
                mResolvingError = true;
                connectionResult.startResolutionForResult(MainActivity.this, REQUEST_RESOLVE_ERROR);
            }catch (IntentSender.SendIntentException e)
            {
                mApiClient.connect();
            }
        } else {
            mResolvingError = true;
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), REQUEST_RESOLVE_ERROR).show();
        }
    }

    /**
     * Override per gestire la risoluzione automatica dell'errore relativo ai Google Play services
     * @author Francesco Moramarco
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                if (!mApiClient.isConnecting() && !mApiClient.isConnected())
                    mApiClient.connect();
            }
        }
    }

    /**
     * per salvare lo stato della variabile booleana mResolving error nel caso in cui l'activity venga killata
     * @author Francesco Moramarco
     * @param outState
     */
    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(RESOLVING_ERROR_STATE_KEY,mResolvingError);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            //L'app non dispone dei permessi necessari
            if ( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION ) )
            {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_locationPermission_alert_dialog)
                        .setMessage(R.string.message_locationPermission_alert_dialog)
                        .setPositiveButton(R.string.grant_button_locationPermission_alert_dialog, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
                            }
                        })
                        .create()
                        .show();
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
            }
        }
        else
        {
            //L'app dispone dei permessi, ora controllo se la geolocalizzaione del dispositivo è attiva
            checkDeviceLocationIsOn();
        }
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, String[]permissions, int[] grantResults )
    {
        if ( requestCode == REQUEST_ACCESS_LOCATION )
        {
            if ( grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                checkDeviceLocationIsOn();
            }
            else
            {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_locationPermission_alert_dialog)
                        .setMessage(R.string.message_locationPermission_alert_dialog)
                        .setPositiveButton(R.string.grant_button_locationPermission_alert_dialog, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION );
                            }
                        })
                        .setNegativeButton(R.string.deny_button_locationPermission_alert_dialog, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
        }
    }

    private void checkDeviceLocationIsOn()
    {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.title_deviceLocation_aler_dialog)
                    .setMessage(R.string.message_deviceLocation_alert_dialog)
                    .setPositiveButton(R.string.openSettings_button_deviceLocation_alert_dialog, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.deny_button_deviceLocation_alert_dialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
        //Dopo aver controllato sia Google Play services, permessi di posizione e GPS dispositivo, parte il service di posizione
        //in background per avere aggiornamenti continui sulla posizione del dispositivo
        startLocationService();
    }

    private void startLocationService()
    {
        if(!isLocationServiceRunning())
        {
            Intent locationServiceIntent = new Intent(this, LocationService.class);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                MainActivity.this.startForegroundService(locationServiceIntent);
            }
            else
                startService(locationServiceIntent);
        }
    }

    private boolean isLocationServiceRunning()
    {
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE) )
        {
            if(LocationService.class.getName().equals(service.service.getClassName()) )
                return true;
        }
        return false;
    }

}
