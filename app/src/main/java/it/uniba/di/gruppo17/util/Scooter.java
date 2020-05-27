package it.uniba.di.gruppo17.util;

import java.util.ArrayList;

public class Scooter {

    public Reporting reportingMaintenance;
    private int idScooter;
    private String latitude;
    private String longitude;
    private String batteryLevel;

    private boolean reqMaintenance;
    public static ArrayList<Scooter> nearScooters;

    public Scooter(int id, String lat, String lon, String batteryLevel )
    {
        this.idScooter = id;
        this.latitude = lat;
        this.longitude = lon;
        this.batteryLevel = batteryLevel;
    }

    public Scooter(int id, String lat, String lon, String batteryLevel, boolean reqMaintenance, Reporting reportingMaintenance )
    {
        this.idScooter = id;
        this.latitude = lat;
        this.longitude = lon;
        this.batteryLevel = batteryLevel;
        this.reqMaintenance = reqMaintenance;
        this.reportingMaintenance =reportingMaintenance;
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

    public static void addNearScooters(ArrayList<Scooter> scootersFromServer )
    {
        if ( nearScooters == null )
            nearScooters = new ArrayList<>();
        else
            nearScooters.clear();
        nearScooters.addAll( scootersFromServer );
    }


    //Metodo per aggiungere ALTRI scooter a quelli già presenti nell'arraylist
    public static void addOtherScooters (ArrayList<Scooter> otherScootersFromServer)
    {
        if ( nearScooters == null )
            nearScooters = new ArrayList<>();

        nearScooters.addAll( otherScootersFromServer );
    }

    public static void clearNearScooters ()
    {
        if (nearScooters == null)
            nearScooters = new ArrayList<>();

        nearScooters.clear();
    }

}
