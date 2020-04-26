package it.uniba.di.gruppo17.util;

import java.util.ArrayList;

public class Scooter {

    private int idScooter;
    private String latitude;
    private String longitude;
    private String batteryLevel;
    public static ArrayList<Scooter> nearScooters;

    public Scooter(int id, String lat, String lon, String batteryLevel )
    {
        this.idScooter = id;
        this.latitude = lat;
        this.longitude = lon;
        this.batteryLevel = batteryLevel;
    }

    public void setIdScooter(int idScooter) {
        this.idScooter = idScooter;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitudine(String longitude) {
        this.longitude = longitude;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getIdScooter() {
        return idScooter;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public static void getNearScooters(ArrayList<Scooter> scootersFromServer )
    {
        if ( nearScooters == null )
            nearScooters = new ArrayList<>();
        else
            nearScooters.clear();
        nearScooters.addAll( scootersFromServer );
    }

}
