package it.uniba.di.gruppo17.util;

public interface
Keys {
    String EMAIL = "email";
    String PASSWORD = "password";
    String RENTALS_TOTAL_DURATION = "rentals_total_duration";
    String SERVER = "http://movinguniba.altervista.org/";
    String SERVER_HOME = "http://movinguniba.altervista.org/login.php";
    String USER_AGENT = "Mozilla/5.0";
    String USER_ID = "id";
    String USER_TYPE = "type";
    String WALLET = "wallet" ;
    String SHARED_PREFERENCES = "MovingFastPreferences";
    String ENCRYPTED_SHARED_PREFERENCES = "MovingFastEncryptedPreferences";
    //Per check conn
    int TIMEOUT = 2500;
    //Per controllo Google play services e permessi geolocalizzazione
    int REQUEST_RESOLVE_ERROR =1 ;
    String RESOLVING_ERROR_STATE_KEY = "RESOLVING_ERROR_STATE_KEY";
    int REQUEST_ACCESS_LOCATION = 2;

    //Per MapsFragment
    int RAGGIO = 2;
    int MAP_ANIMATION_DURATION = 600;
    float ZOOM = 13.0f;

    //rentFragment
    String RENT = "rent";
    String CLOSE = "close";

    //resultfragment
    String IN_RENT = "in_rent";
    String RENT_ID = "id_rent";
    String SCOOTER_ID = "id_scooter";
    String CHRONOMETER_TIME = "chronometer_time";
    String CURRENT_DATE_TIME ="current_date_time";
    String PATTERN_DATE_TIME = "dd-MM-yyyy HH:mm:ss";
    String TRAVELED_DISTANCE = "traveled_distance";

    //result close fragment
    float UNLOCK_COST = 1.00f;
    float COST_PER_MINUTE = 0.10f;

}
