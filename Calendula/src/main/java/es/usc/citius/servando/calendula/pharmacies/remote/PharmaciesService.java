package es.usc.citius.servando.calendula.pharmacies.remote;

import java.util.List;

import es.usc.citius.servando.calendula.pharmacies.persistance.Pharmacy;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Path;

/**
 * Created by isaac@isaaccastro.eu on 10/08/16.
 */

public interface PharmaciesService {

    /**
     * Get all pharmacies
     * @param open if this parameter is empty, method returns all data, otherwise returns only open pharmacies
     * @return
     */
    @GET("all/{open}")
    Call<List<Pharmacy>> listAll(@Path("open") String open);

    /**
     * Get pharmacies with name like the "name" parameter
     * @param name name or part of it to filter
     * @param open if this parameter is empty, method returns all data, otherwise returns only open pharmacies
     * @return
     */
    @GET("name/{name}/{open}")
    Call<List<Pharmacy>> listByName(@Path("name") String name, @Path("open") String open);

    /**
     * Get pharmacies with name equals "postcode" parameter
     * @param postcode postcode of the parmacies
     * @param open if this parameter is empty, method returns all data, otherwise returns only open pharmacies
     * @return
     */
    @GET("postcode/{postcode}/{open}")
    Call<List<Pharmacy>> listByPostcode(@Path("postcode") String postcode, @Path("open") String open);

    /**
     * Get pharmacies in the town specified
     * @param town town of the pharmacies
     * @param open if this parameter is empty, method returns all data, otherwise returns only open pharmacies
     * @return
     */
    @GET("town/{town}/{open}")
    Call<List<Pharmacy>> listByTown(@Path("town") String town, @Path("open") String open);

    /**
     * Get pharmacies with address like "address" specified
     * @param address
     * @param open if this parameter is empty, method returns all data, otherwise returns only open pharmacies
     * @return
     */
    @GET("address/{address}/{open}")
    Call<List<Pharmacy>> listByAddress(@Path("address") String address, @Path("open") String open);

    /**
     * Get pharmacies open in some date
     * @param date
     * @return
     */
    @GET("guards/{date}")
    Call<List<Pharmacy>> listByGuars(@Path("date") String date);

    /**
     * Get pharmacies from a point to a certain distance
     * @param latitude latitude part of a point
     * @param longitude longitude part of a point
     * @param distance meters from the point to look for pharmacies
     * @param open if this parameter is empty, method returns all data, otherwise returns only open pharmacies
     * @return
     */
    @GET("location/{lat}/{long}/{distance}/{open}")
    Call<List<Pharmacy>> listByLocation(@Path("lat") Double latitude, @Path("long") Double longitude, @Path("distance") Integer distance, @Path("open") String open);

    /**
     * Get nearest pharmacy from a point to a certain distance
     * @param latitude latitude part of a point
     * @param longitude longitude part of a point
     * @param distance meters from the point to look for pharmacies
     * @param open if this parameter is empty, method returns all data, otherwise returns only open pharmacies
     * @return
     */
    @GET("nextPharmacy/{lat}/{long}/{distance}/{open}")
    Call<List<Pharmacy>> getNearest(@Path("lat") Double latitude, @Path("long") Double longitude, @Path("distance") Integer distance,  @Path("open") String open);

    @GET("search/{query}")
    Call<List<Pharmacy>> listByUserSearch(@Path("query") String query);
}
