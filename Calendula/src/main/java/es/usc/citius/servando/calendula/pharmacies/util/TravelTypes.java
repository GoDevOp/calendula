package es.usc.citius.servando.calendula.pharmacies.util;

/**
 * Created by isaac@isaaccastro.eu on 12/11/16.
 */

public enum TravelTypes {
    CAR("driving"), WALK("walking"), BICYCLE("bicycling"), PUBLIC("transit");

    private String value;

    private TravelTypes(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}