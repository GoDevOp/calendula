package es.usc.citius.servando.calendula.pharmacies.util;

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

/**
 * Created by isaac@isaaccastro.eu on 12/11/16.
 */

public class TimeTravel {

    public static String getTime(Float latitudeOrigin, Float longitudeOrigin, Float latitudeDestination, Float longitudeDestination, String mode){
        // Create URL
        StringBuilder urlStr = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        urlStr.append("origin=");
        urlStr.append(latitudeOrigin.toString());
        urlStr.append(",");
        urlStr.append(longitudeOrigin.toString());
        urlStr.append("&destination=");
        urlStr.append(latitudeDestination.toString());
        urlStr.append(",");
        urlStr.append(longitudeDestination.toString());
        urlStr.append("&mode=");
        urlStr.append(mode);
        urlStr.append("&key=AIzaSyAm3gggVOKXyJra2a7OwJXOdYTjVWhs1F0");

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
            // routesArray contains ALL routes
            JSONArray routesArray = jsonObject.getJSONArray("routes");
            // Grab the first route
            JSONObject route = routesArray.getJSONObject(0);
            // Take all legs from the route
            JSONArray legs = route.getJSONArray("legs");
            // Grab first leg
            JSONObject leg = legs.getJSONObject(0);

            JSONObject durationObject = leg.getJSONObject("duration");
            String duration = durationObject.getString("text");

            return duration;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
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
