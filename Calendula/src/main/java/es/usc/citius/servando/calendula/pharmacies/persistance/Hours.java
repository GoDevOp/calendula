package es.usc.citius.servando.calendula.pharmacies.persistance;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by isaac on 19/9/16.
 */
public class Hours {

    private String description;
    private Date openHourMorning;
    private Date closeHourMorning;
    private Date openHourAfternoon;
    private Date closeHourAfternoon;
    private ArrayList<Integer> weekDays;

    public Hours() {
        weekDays = new ArrayList<Integer>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getOpenHourMorning() {
        return openHourMorning;
    }

    public void setOpenHourMorning(Date openHourMorning) {
        this.openHourMorning = openHourMorning;
    }

    public Date getCloseHourMorning() {
        return closeHourMorning;
    }

    public void setCloseHourMorning(Date closeHourMorning) {
        this.closeHourMorning = closeHourMorning;
    }

    public Date getOpenHourAfternoon() {
        return openHourAfternoon;
    }

    public void setOpenHourAfternoon(Date openHourAfternoon) {
        this.openHourAfternoon = openHourAfternoon;
    }

    public Date getCloseHourAfternoon() {
        return closeHourAfternoon;
    }

    public void setCloseHourAfternoon(Date closeHourAfternoon) {
        this.closeHourAfternoon = closeHourAfternoon;
    }

    public ArrayList<Integer> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(ArrayList<Integer> weekDays) {
        this.weekDays = weekDays;
    }
}
