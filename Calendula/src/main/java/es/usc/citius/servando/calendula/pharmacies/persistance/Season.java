package es.usc.citius.servando.calendula.pharmacies.persistance;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by isaac on 19/9/16.
 */
public class Season {

    private Date startDate;
    private Date endDate;
    private ArrayList<Hours> hours;

    public Season() {
        hours = new ArrayList<Hours>();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ArrayList<Hours> getHours() {
        return hours;
    }

    public void setHours(ArrayList<Hours> hours) {
        this.hours = hours;
    }
}
