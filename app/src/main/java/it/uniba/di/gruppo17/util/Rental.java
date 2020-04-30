package it.uniba.di.gruppo17.util;

public class Rental {

    private int giorno;
    private int mese;
    private int anno;
    private float durata;
    private int importo;

    public Rental(float durata){
        this.durata = durata;
    }

    public float getDurata(){
        return this.durata;
    }

    public void setDurata(float durata){
        this.durata = durata;
    }


}
