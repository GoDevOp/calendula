package es.usc.citius.servando.calendula.pharmacies.persistance;

import android.icu.util.TimeZone;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import es.usc.citius.servando.calendula.pharmacies.util.Utils;

/**
 * Created by isaac on 19/9/16.
 */
public class Pharmacy implements Parcelable {

    private Integer codPharmacy;
    private String name;
    private String address;
    private String town;
    private String postCode;
    private String phone;
    private Float[] gps;
    private String notes;
    private ArrayList<Holiday> holidays;
    private ArrayList<Calendar> calendar;

    public Pharmacy() {
        holidays = new ArrayList<Holiday>();
        calendar = new ArrayList<Calendar>();
    }

    protected Pharmacy(Parcel in) {
        name = in.readString();
        address = in.readString();
        town = in.readString();
        postCode = in.readString();
        phone = in.readString();
        notes = in.readString();
    }

    public static final Creator<Pharmacy> CREATOR = new Creator<Pharmacy>() {
        @Override
        public Pharmacy createFromParcel(Parcel in) {
            return new Pharmacy(in);
        }

        @Override
        public Pharmacy[] newArray(int size) {
            return new Pharmacy[size];
        }
    };

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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ArrayList<Holiday> getHolidays() {
        return holidays;
    }

    public void setHolidays(ArrayList<Holiday> holidays) {
        this.holidays = holidays;
    }

    public Float[] getGps() {
        return gps;
    }

    public void setGps(Float[] gps) {
        this.gps = gps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(town);
        dest.writeString(postCode);
        dest.writeString(phone);
        dest.writeString(notes);
    }

    public boolean isOpen(){

        Boolean open = null;

        Date date = new Date();
        java.util.Calendar now = java.util.Calendar.getInstance();
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH), now.get(java.util.Calendar.DAY_OF_MONTH), 0, 0, 0);
        Date dateWithoutTime = cal.getTime();

        cal.set(now.get(java.util.Calendar.YEAR), 0, 1, now.get(java.util.Calendar.HOUR_OF_DAY), now.get(java.util.Calendar.MINUTE), 0);
        Date timeWidtoutDate = cal.getTime();

        GregorianCalendar hour0 = new GregorianCalendar();
        hour0.setTimeInMillis(1451602800000l); // 01/10/2016 00:00:00
        GregorianCalendar hour2359 = new GregorianCalendar();
        hour2359.set(now.get(java.util.Calendar.YEAR), 0, 1, 23, 59, 59);

        for (Calendar calendar:this.calendar){
            if (calendar.getGuards().contains(dateWithoutTime)){
                Log.d("PHARMACY OPEN", "Pharmacy "+this.getName()+" open because guard");
                open = true;
                break;
            }

            if (this.getHolidays().contains(dateWithoutTime)){
                Log.d("PHARMACY CLOSED", "Pharmacy "+this.getName()+" closed because holiday");
                open = false;
                break;
            }

            for (Season season:calendar.getSeasons()){
                if (dateWithoutTime.after(season.getStartDate()) && dateWithoutTime.before(season.getEndDate())){
                    for (Hours hours:season.getHours()){

                        Date openHourMorning = hours.getOpenHourMorning();
                        Date closeHourMorning = hours.getCloseHourMorning();
                        Date openHourAfternoon = hours.getOpenHourAfternoon();
                        Date closeHourAfternoon = hours.getCloseHourAfternoon();

                        if (openHourMorning != null) {
                            openHourMorning = getDayWithOpenTime(hours.getOpenHourMorning());
                        }
                        if (closeHourMorning != null) {
                            closeHourMorning = getDayWithOpenTime(hours.getCloseHourMorning());
                        }
                        if (openHourAfternoon != null) {
                            openHourAfternoon = getDayWithOpenTime(hours.getOpenHourAfternoon());
                        }
                        if (closeHourAfternoon != null) {
                            closeHourAfternoon = getDayWithOpenTime(hours.getCloseHourAfternoon());
                        }

                        // if close hour is before open hour it's because close after 00:00
                        if (closeHourMorning != null && openHourMorning != null &&
                                closeHourMorning.before(openHourMorning)){
                            closeHourMorning = Utils.addDays(closeHourMorning, 1);
                        }
                        if (closeHourAfternoon != null && openHourAfternoon != null &&
                                closeHourAfternoon.before(openHourAfternoon)){
                            closeHourAfternoon = Utils.addDays(closeHourAfternoon, 1);
                        }

                        if (closeHourMorning != null && closeHourMorning.compareTo(hour0.getTime()) == 0){
                            hours.setCloseHourMorning(hour2359.getTime());
                        }
                        if (closeHourAfternoon != null && closeHourAfternoon.compareTo(hour0.getTime()) == 0){
                            hours.setCloseHourAfternoon(hour2359.getTime());
                        }

                        if (hours.getWeekDays().contains(now.get(java.util.Calendar.DAY_OF_WEEK)-1) &&
                                (openHourMorning != null && date.after(openHourMorning) &&
                                 closeHourMorning != null && date.before(closeHourMorning)) ||

                                (openHourAfternoon != null && date.after(openHourAfternoon) &&
                                 closeHourAfternoon !=null && date.before(closeHourAfternoon))){
                            Log.d("PHARMACY OPEN", "Pharmacy "+this.getName()+" open because hours");
                            open = true;
                            break;
                        }
                    }
                }
                if (open != null){
                    break;
                }
            }
        }

        if (open == null){
            open = false;
        }

        return open;
    }

    private Date getDayWithOpenTime(Date date){
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar openHour = java.util.Calendar.getInstance();
        openHour.setTime(date);
        GregorianCalendar cal = new GregorianCalendar();

        cal.set(now.get(java.util.Calendar.YEAR),
                now.get(java.util.Calendar.MONTH),
                now.get(java.util.Calendar.DAY_OF_MONTH),
                openHour.get(java.util.Calendar.HOUR_OF_DAY),
                openHour.get(java.util.Calendar.MINUTE),
                0);

        return cal.getTime();
    }

    public String getHours(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        GregorianCalendar cal = new GregorianCalendar();
        java.util.Calendar now = java.util.Calendar.getInstance();
        cal.set(now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH), now.get(java.util.Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        Date dateWithoutTime = cal.getTime();
        String strHours = "";

        for (Calendar calendar:this.calendar){
            for (Season season:calendar.getSeasons()){
                if (dateWithoutTime.after(season.getStartDate()) && dateWithoutTime.before(season.getEndDate())) {
                    for (Hours hours : season.getHours()) {
                        if (hours.getWeekDays().contains(now.get(java.util.Calendar.DAY_OF_WEEK)-1)){
                            if (hours.getOpenHourMorning() != null &&  hours.getCloseHourMorning() != null) {
                                strHours += sdf.format(hours.getOpenHourMorning()) + " - ";
                                strHours += sdf.format(hours.getCloseHourMorning());
                                strHours += "\n";
                            }
                            if (hours.getOpenHourAfternoon() != null &&  hours.getCloseHourAfternoon() != null) {
                                strHours += sdf.format(hours.getOpenHourAfternoon()) + " - ";
                                strHours += sdf.format(hours.getCloseHourAfternoon());
                            }
                            break;
                        }
                    }
                }
            }
        }

        return strHours;
    }
}
