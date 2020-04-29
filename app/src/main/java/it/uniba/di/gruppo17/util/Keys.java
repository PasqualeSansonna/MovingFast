package it.uniba.di.gruppo17.util;

public interface Keys {
    String EMAIL = "email";
    String PASSWORD = "password";
    String SERVER = "http://192.168.178.22/";
    String USER_AGENT = "Mozilla/5.0";
    String ID_UTENTE = "id";
    String SHARED_PREFERENCES = "MovingFastPreferences";
    //Per controllo Google play services e permessi geolocalizzazione
    int REQUEST_RESOLVE_ERROR =1 ;
    String RESOLVING_ERROR_STATE_KEY = "RESOLVING_ERROR_STATE_KEY";
    int REQUEST_ACCESS_LOCATION = 2;

    //Per MapsFragment
    int RAGGIO = 2;
    int MAP_ANIMATION_DURATION = 600;
    float ZOOM = 13.0f;
}
