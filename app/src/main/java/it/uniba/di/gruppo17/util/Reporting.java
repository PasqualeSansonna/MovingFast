package it.uniba.di.gruppo17.util;

public class Reporting {
    private int id;
    private boolean brakes;
    private boolean wheels;
    private boolean handlebars;
    private String description;

    public Reporting (int id, boolean brakes, boolean wheels, boolean handlebars, String description)
    {
        this.id = id;
        this.brakes = brakes;
        this.wheels = wheels;
        this.handlebars = handlebars;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return  brakes;
    }

    public boolean isHandlebarsBroken()
    {
        return handlebars;
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
}
