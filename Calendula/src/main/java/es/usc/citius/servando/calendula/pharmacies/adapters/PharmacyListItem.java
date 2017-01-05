package es.usc.citius.servando.calendula.pharmacies.adapters;

/**
 * Created by isaac@isaaccastro.eu on 3/12/16.
 */

public class PharmacyListItem {

    private Integer codPharmacy;
    private String name;
    private String address;
    private String timeTravelCar;
    private String timeTravelTransit;
    private String timeTravelWalking;
    private String timeTravelBicycle;
    private String distanceCar;
    private String distanceTransit;
    private String distanceWalking;
    private String distanceBicycle;
    private boolean open;
    private boolean guard;

    public Integer getCodPharmacy() {
        return codPharmacy;
    }

    public void setCodPharmacy(Integer codPharmacy) {
        this.codPharmacy = codPharmacy;
    }

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

    public String getDistanceCar() {
        return distanceCar;
    }

    public void setDistanceCar(String distanceCar) {
        this.distanceCar = distanceCar;
    }

    public String getDistanceTransit() {
        return distanceTransit;
    }

    public void setDistanceTransit(String distanceTransit) {
        this.distanceTransit = distanceTransit;
    }

    public String getDistanceWalking() {
        return distanceWalking;
    }

    public void setDistanceWalking(String distanceWalking) {
        this.distanceWalking = distanceWalking;
    }

    public String getDistanceBicycle() {
        return distanceBicycle;
    }

    public void setDistanceBicycle(String distanceBicycle) {
        this.distanceBicycle = distanceBicycle;
    }

    public boolean isGuard() {
        return guard;
    }

    public void setGuard(boolean guard) {
        this.guard = guard;
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
