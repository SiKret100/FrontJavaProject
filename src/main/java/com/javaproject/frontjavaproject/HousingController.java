package com.javaproject.frontjavaproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HousingController {

    public static JSONArray fetchHousing(String region, String market, String type, JSONArray combinedResponse) throws IOException, RuntimeException {

        String encodedRegion = URLEncoder.encode(region, StandardCharsets.UTF_8.toString());
        String encodedMarket = URLEncoder.encode(market, StandardCharsets.UTF_8.toString());
        String encodedType = URLEncoder.encode(type, StandardCharsets.UTF_8.toString());

        String MarketUrl = String.format(
                "http://localhost:8080/api/housingPrices/?name=%s&transaction=%s&surface=%s",
                encodedRegion, encodedMarket, encodedType);

        URL secondaryUrl = new URL(MarketUrl);
        HttpURLConnection connection = (HttpURLConnection) secondaryUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        String token = AuthManager.getToken();
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseBuilder.append(inputLine);
            }
            in.close();

            JSONArray response = new JSONArray(responseBuilder.toString());
            for (int i = 0; i < response.length(); i++) {
                combinedResponse.put(response.getJSONObject(i));
            }

            return combinedResponse;

        } else {
            throw new IOException("GET request failed with response code: " + responseCode);
        }
    }

    public static void addHousingPrice(String region, String market, String type, String price) throws IOException, RuntimeException {

        if (!checkForm(region, market, type, price)) {
            throw new IllegalArgumentException("Form validation failed. Housing price not added.");
        }

        String encodedRegion = URLEncoder.encode(region, StandardCharsets.UTF_8.toString());
        String encodedMarket = URLEncoder.encode(market, StandardCharsets.UTF_8.toString());
        String encodedType = URLEncoder.encode(type, StandardCharsets.UTF_8.toString());

        Integer lastYear = getLastYear(region, market, type);

        if (lastYear == -1) {
            throw new RuntimeException("Error getting last year.");
        }

        JSONObject housingData = new JSONObject();
        housingData.put("name", region);
        housingData.put("transaction", market);
        housingData.put("surface", type);
        housingData.put("year", lastYear + 1);
        housingData.put("price", Integer.parseInt(price));

        String apiUrl = "http://localhost:8080/api/housingPrices/";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String token = AuthManager.getToken();
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }

        OutputStream os = connection.getOutputStream();
        os.write(housingData.toString().getBytes(StandardCharsets.UTF_8));
        os.close();

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
            throw new IOException("Failed to add housing price. Response code: " + responseCode);
        }
    }

    public static Integer getLastYear(String region, String market, String type) throws IOException {

        JSONArray combinedResponse = new JSONArray();
        combinedResponse = fetchHousing(region, market, type, combinedResponse);
        return combinedResponse.getJSONObject(combinedResponse.length() - 1).getInt("year");
    }

    public static Boolean checkForm(String region, String market, String type, String price) {
        if (region != null && !region.equals("Choose Region") &&
                market != null && !market.equals("Choose Market") &&
                type != null && !type.equals("Choose Type") &&
                price != null && !price.isBlank()) {
            try {
                Integer.parseInt(price);
                return true;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid price format: " + price);
            }
        } else {
            throw new IllegalArgumentException("One or more fields have default values or are empty.");
        }
    }
}
