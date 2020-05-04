package it.uniba.di.gruppo17.util;


import android.os.Parcel;
import android.os.Parcelable;

/** @author Pasquale
 *  Classe relativa ai noleggi che implementa l'interfaccia Parcelable per la serializzazione
 *  degli oggetti in quanto utile nel passaggio degli stessi nella intent della cardView
 *  (all'interno dello storico noleggi)
 */
public class Rental implements Parcelable {

    private String data;
    private String ora_inizio;
    private String ora_fine;
    private String longitudine_arrivo;
    private String latitudine_arrivo;
    private String longitudine_partenza;
    private String latitudine_partenza;
    private Integer durata;
    private Float importo;

    public Rental(String data, String ora_inizio, String ora_fine, Integer durata, Float importo, String longitudine_arrivo, String latitudine_arrivo, String longitudine_partenza, String latitudine_partenza ) {
        this.data = data;
        this.ora_inizio = ora_inizio;
        this.ora_fine = ora_fine;
        this.durata = durata;
        this.importo = importo;
        this.longitudine_arrivo = longitudine_arrivo;
        this.latitudine_arrivo = latitudine_arrivo;
        this.longitudine_partenza = longitudine_partenza;
        this.latitudine_partenza = latitudine_partenza;
    }

    public Rental(Parcel parcel){
        this.latitudine_partenza = parcel.readString();
        this.longitudine_partenza = parcel.readString();
        this.latitudine_arrivo = parcel.readString();
        this.longitudine_arrivo = parcel.readString();
    }



    public String getData() {
        return data;
    }

    public String getOraInizio() {
        return ora_inizio;
    }

    public String getOraFine() {
        return ora_fine;
    }

    public String getLongitudine_arrivo() {
        return longitudine_arrivo;
    }

    public String getLatitudine_arrivo() {
        return latitudine_arrivo;
    }

    public String getLongitudine_partenza() {
        return longitudine_partenza;
    }

    public String getLatitudine_partenza() {
        return latitudine_partenza;
    }

    public Float getImporto() {
        return importo;
    }

    public Integer getDurata(){
        return durata;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(latitudine_partenza);
        dest.writeString(longitudine_partenza);
        dest.writeString(latitudine_arrivo);
        dest.writeString(longitudine_arrivo);
    }

    public final static Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Rental createFromParcel(Parcel source) {
            return new Rental(source);
        }

        @Override
        public Rental[] newArray(int size) {
            return new Rental[size];
        }
    };
}
