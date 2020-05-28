package it.uniba.di.gruppo17.util;

public class Reporting {
    private int idScooter;
    private int idReporting;
    private int idUser;
    private boolean brakes;
    private boolean wheels;
    private boolean handlebars;
    private boolean accelerator;
    private boolean lock;
    private boolean other;
    private String description;

    public Reporting (int idScooter, int idReporting, boolean brakes, boolean wheels, boolean handlebars, boolean accelerator, boolean lock, boolean other, String description)
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

    public Reporting (int idScooter, int idReporting, int idUser, boolean brakes, boolean wheels, boolean handlebars, boolean accelerator, boolean lock, boolean other, String description)
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

    public boolean isBrakes() {
        return brakes;
    }

    public boolean isWheels() {
        return wheels;
    }

    public boolean isHandlebars() {
        return handlebars;
    }

    public boolean isAccelerator() {
        return accelerator;
    }

    public boolean isLock() {
        return lock;
    }

    public boolean isOther() {
        return other;
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

    public boolean isBrakesBroken()
    {
        return brakes;
    }

    public boolean isWheelsBroken()
    {
        return  wheels;
    }

    public boolean isHandlebarsBroken()
    {
        return handlebars;
    }

    public boolean isAcceleratorBroken()
    {
        return  accelerator;
    }

    public boolean isLockBroken()
    {
        return  lock;
    }

    public boolean isOtherBroken()
    {
        return  other;
    }

    public void setBrakes(boolean brakes) {
        this.brakes = brakes;
    }

    public void setHandlebars(boolean handlebars) {
        this.handlebars = handlebars;
    }

    public void setWheels (boolean wheels)
    {
        this.wheels = wheels;
    }

    public void setAccelerator(boolean accelerator) {
        this.accelerator = accelerator;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public void setOther(boolean other) {
        this.other = other;
    }
}
