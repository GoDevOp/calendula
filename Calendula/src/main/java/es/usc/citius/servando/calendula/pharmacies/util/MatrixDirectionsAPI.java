package es.usc.citius.servando.calendula.pharmacies.util;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.usc.citius.servando.calendula.pharmacies.persistance.MatrixDirectionsApiResponse;
import es.usc.citius.servando.calendula.pharmacies.persistance.Pharmacy;

/**
 * Created by isaac@isaaccastro.eu on 12/11/16.
 */

public class MatrixDirectionsAPI {

    public static HashMap<Integer, MatrixDirectionsApiResponse> getTime(Location origin,
                                                                        List<Pharmacy> destinationPharmacies,
                                                                        String mode){

        HashMap<Integer, MatrixDirectionsApiResponse> result = new HashMap<Integer, MatrixDirectionsApiResponse>();

        // Create URL
        StringBuilder urlStr = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?");
        urlStr.append("origins=");
        urlStr.append(String.valueOf(origin.getLatitude()));
        urlStr.append(",");
        urlStr.append(String.valueOf(origin.getLongitude()));
        urlStr.append("&destinations=");
        for (Pharmacy pharmacy:destinationPharmacies){
            urlStr.append(String.valueOf(pharmacy.getGps()[1]));
            urlStr.append(",");
            urlStr.append(String.valueOf(pharmacy.getGps()[0]));
            urlStr.append("|");
        }
        urlStr.deleteCharAt(urlStr.length()-1);
        urlStr.append("&mode=");
        urlStr.append(mode);
        urlStr.append("&key=AIzaSyAIy2TbU3TVPEopMa0T68dDr58FucNHE1Y");

        URL url = null;
        try {
            url = new URL(urlStr.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("getTime", "The response is: " + response);
            InputStream is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIS(is);

            JSONObject jsonObject = new JSONObject(contentAsString);
            // rowsArray contains ALL routes
            JSONArray rowsArray = jsonObject.getJSONArray("rows");
            // Grab the first route
            if (rowsArray != null && rowsArray.length() > 0) {
                JSONObject elementsObject = rowsArray.getJSONObject(0);
                JSONArray elementsArray = elementsObject.getJSONArray("elements");
                for (int i=0; i < elementsArray.length(); i++){
                    MatrixDirectionsApiResponse data = new MatrixDirectionsApiResponse();
                    String distance = "";
                    String duration = "";
                    JSONObject element = elementsArray.getJSONObject(i);
                    String status = element.getString("status");
                    if (status.equals("OK")){
                        JSONObject durationObject = element.getJSONObject("duration");
                        duration = durationObject.getString("value");

                        JSONObject distanceObject = element.getJSONObject("distance");
                        distance = distanceObject.getString("value");

                        data.setTime(duration);
                        data.setDistance(distance);
                    }
                    // Associate in hashmap time with pharmacy
                    result.put(destinationPharmacies.get(i).getCodPharmacy(), data);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    static public String readIS(InputStream entityResponse) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = entityResponse.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString();
    }

}
