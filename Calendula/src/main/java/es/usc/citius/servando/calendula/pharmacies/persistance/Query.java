package es.usc.citius.servando.calendula.pharmacies.persistance;

/**
 * Created by isaac@isaaccastro.eu on 29/10/16.
 */

public class Query {

    private Double latitude;
    private Double longitude;
    private Integer radio;

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    private int queryType;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRadio() {
        return radio;
    }

    public void setRadio(Integer radio) {
        this.radio = radio;
    }
}
