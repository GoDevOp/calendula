package es.usc.citius.servando.calendula.pharmacies.persistance;

import android.icu.util.TimeZone;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

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
        cal.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
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

                        Date closeHourMorning = hours.getCloseHourMorning();
                        Date closeHourAfternoon = hours.getCloseHourAfternoon();

                        if (closeHourMorning != null && closeHourMorning.compareTo(hour0.getTime()) == 0){
                            hours.setCloseHourMorning(hour2359.getTime());
                        }
                        if (closeHourAfternoon != null && closeHourAfternoon.compareTo(hour0.getTime()) == 0){
                            hours.setCloseHourAfternoon(hour2359.getTime());
                        }

                        if (hours.getWeekDays().contains(now.get(java.util.Calendar.DAY_OF_WEEK)) &&
                                (hours.getOpenHourMorning()!= null && timeWidtoutDate.after(hours.getOpenHourMorning()) && hours.getCloseHourMorning() != null && timeWidtoutDate.before(hours.getCloseHourMorning())) ||
                                (hours.getOpenHourAfternoon() != null && timeWidtoutDate.after(hours.getOpenHourAfternoon()) && hours.getCloseHourAfternoon()!=null && timeWidtoutDate.before(hours.getCloseHourAfternoon()))){
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
}
