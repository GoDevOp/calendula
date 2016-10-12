package es.usc.citius.servando.calendula.pharmacies.persistance;

import java.util.Date;

/**
 * Created by isaac on 19/9/16.
 */
public class Holiday {

    private Date date;
    private String name;
    private String type;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
