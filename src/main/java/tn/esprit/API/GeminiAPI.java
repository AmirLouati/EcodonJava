package tn.esprit.API;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeminiAPI {
    private static final String API_KEY = "AIzaSyBisr8CgSvo4qndtToLgwHL56nDrS776ec";
    private static final String URL_STRING = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;


    public static String detectLanguage(String text) {
        return sendToGemini("Detect the language of this text: " + text);
    }

    public static String translateText(String text, String targetLanguage) {
        return sendToGemini("Translate this text to " + targetLanguage + ": " + text);
    }

    public static String sendToGemini(String userInput) {
        try {
            // Corrected request body
            JSONObject requestBody = new JSONObject();
            JSONArray contentsArray = new JSONArray();
            JSONObject contentObject = new JSONObject();
            JSONArray partsArray = new JSONArray();
            JSONObject textObject = new JSONObject();

            textObject.put("text", userInput);
            partsArray.put(textObject);
            contentObject.put("parts", partsArray);
            contentsArray.put(contentObject);
            requestBody.put("contents", contentsArray);

            // Sending request
            URL url = new URL(URL_STRING);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Reading response
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;

            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            return parseResponse(response.toString());
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private static String parseResponse(String jsonResponse) {
        try {
            JSONObject response = new JSONObject(jsonResponse);
            if (response.has("candidates")) {
                JSONArray candidates = response.getJSONArray("candidates");
                if (candidates.length() > 0) {
                    JSONObject firstCandidate = candidates.getJSONObject(0);
                    if (firstCandidate.has("content")) {
                        JSONObject content = firstCandidate.getJSONObject("content");
                        if (content.has("parts")) {
                            JSONArray parts = content.getJSONArray("parts");
                            if (parts.length() > 0) {
                                return parts.getJSONObject(0).getString("text");
                            }
                        }
                    }
                }
            }
            return "No valid response.";
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
    }
}
