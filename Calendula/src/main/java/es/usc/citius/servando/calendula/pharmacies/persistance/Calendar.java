package es.usc.citius.servando.calendula.pharmacies.persistance;

import java.util.ArrayList;

/**
 * Created by isaac on 19/9/16.
 */
public class Calendar {

    private ArrayList<Guard> guards;
    private ArrayList<Season> seasons;

    public Calendar() {
        guards = new ArrayList<Guard>();
        seasons = new ArrayList<Season>();
    }

    public ArrayList<Guard> getGuards() {
        return guards;
    }

    public void setGuards(ArrayList<Guard> guards) {
        this.guards = guards;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(ArrayList<Season> seasons) {
        this.seasons = seasons;
    }
}
