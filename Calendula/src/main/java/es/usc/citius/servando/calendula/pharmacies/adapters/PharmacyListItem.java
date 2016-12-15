package es.usc.citius.servando.calendula.pharmacies.adapters;

/**
 * Created by isaac@isaaccastro.eu on 3/12/16.
 */

public class PharmacyListItem {

    private String name;
    private String address;
    private String timeTravelCar;
    private String timeTravelTransit;
    private String timeTravelWalking;
    private String timeTravelBicycle;
    private boolean open;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimeTravelCar() {
        return timeTravelCar;
    }

    public void setTimeTravelCar(String timeTravelCar) {
        this.timeTravelCar = timeTravelCar;
    }

    public String getTimeTravelTransit() {
        return timeTravelTransit;
    }

    public void setTimeTravelTransit(String timeTravelTransit) {
        this.timeTravelTransit = timeTravelTransit;
    }

    public String getTimeTravelWalking() {
        return timeTravelWalking;
    }

    public void setTimeTravelWalking(String timeTravelWalking) {
        this.timeTravelWalking = timeTravelWalking;
    }

    public String getTimeTravelBicycle() {
        return timeTravelBicycle;
    }

    public void setTimeTravelBicycle(String timeTravelBicycle) {
        this.timeTravelBicycle = timeTravelBicycle;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public String toString() {
        return "PharmacyListItem{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", timeTravelCar='" + timeTravelCar + '\'' +
                ", timeTravelTransit='" + timeTravelTransit + '\'' +
                ", timeTravelWalking='" + timeTravelWalking + '\'' +
                ", timeTravelBicycle='" + timeTravelBicycle + '\'' +
                ", open=" + open +
                '}';
    }
}
