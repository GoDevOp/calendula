package es.usc.citius.servando.calendula.pharmacies.persistance;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

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

    }
}
