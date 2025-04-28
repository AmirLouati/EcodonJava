package tn.esprit.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class GeocodingService {
    private static final String API_KEY = "8d0ccf31d31e445781daf8aa81baeb71"; // Get from OpenCage or similar service
    private static final String GEOCODING_URL = "https://api.opencagedata.com/geocode/v1/json";

    public String getCountryFromCoordinates(double latitude, double longitude) throws Exception {
        String query = String.format("%f+%f", latitude, longitude);
        String urlString = String.format("%s?q=%s&key=%s&language=en",
                GEOCODING_URL, query, API_KEY);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed to geocode: HTTP error code " + conn.getResponseCode());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject json = new JSONObject(response.toString());
        JSONObject result = json.getJSONArray("results").getJSONObject(0);
        JSONObject components = result.getJSONObject("components");

        return components.getString("country");
    }
}