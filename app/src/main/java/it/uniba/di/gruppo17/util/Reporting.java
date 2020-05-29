package it.uniba.di.gruppo17.util;

public class Reporting {
    private int idScooter;
    private int idReporting;
    private int idUser;
    private int brakes;
    private int wheels;
    private int handlebars;
    private int accelerator;
    private int lock;
    private int other;
    private String description;

    public Reporting (int idScooter, int idReporting, int brakes, int wheels, int handlebars, int accelerator, int lock, int other, String description)
    {
        this.idScooter = idScooter;
        this.idReporting = idReporting;
        this.brakes = brakes;
        this.wheels = wheels;
        this.handlebars = handlebars;
        this.accelerator = accelerator;
        this.lock = lock;
        this.other = other;
        this.description = description;
    }

    public Reporting (int idScooter, int idReporting, int idUser, int brakes, int wheels, int handlebars, int accelerator, int lock, int other, String description)
    {
        this.idScooter = idScooter;
        this.idReporting = idReporting;
        this.idUser = idUser;
        this.brakes = brakes;
        this.wheels = wheels;
        this.handlebars = handlebars;
        this.accelerator = accelerator;
        this.lock = lock;
        this.other = other;
        this.description = description;
    }


    public int getIdUser() {
        return idUser;
    }

    public int getIdScooter() {
        return idScooter;
    }

    public void setIdScooter(int id) {
        this.idScooter = id;
    }

    public int getIdReporting() {
        return idReporting;
    }

    public void setIdReporting(int idReporting) {
        this.idReporting = idReporting;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int isBrakesBroken()
    {
        return brakes;
    }

    public int isWheelsBroken()
    {
        return  wheels;
    }

    public int isHandlebarsBroken()
    {
        return handlebars;
    }

    public int isAcceleratorBroken()
    {
        return  accelerator;
    }

    public int isLockBroken()
    {
        return  lock;
    }

    public int isOtherBroken()
    {
        return  other;
    }

    public void setBrakes(int brakes) {
        this.brakes = brakes;
    }

    public void setHandlebars(int handlebars) {
        this.handlebars = handlebars;
    }

    public void setWheels (int wheels)
    {
        this.wheels = wheels;
    }

    public void setAccelerator(int accelerator) {
        this.accelerator = accelerator;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public void setOther(int other) {
        this.other = other;
    }
}

