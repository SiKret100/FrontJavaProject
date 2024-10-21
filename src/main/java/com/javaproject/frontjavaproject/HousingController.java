package com.javaproject.frontjavaproject;

import com.javaproject.frontjavaproject.authorization.AuthManager;
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
import java.util.StringJoiner;

public class HousingController {

    public static JSONArray fetchHousing(String region, String market, String type, JSONArray combinedResponse) throws IOException, RuntimeException {

        String encodedRegion;//name
        String encodedMarket;//transaction
        String encodedType;//surface
        String MarketUrl;

        if(region == null && market == null && type == null) {
            MarketUrl = "http://localhost:8080/api/housingPrices/";
        } else {

            StringJoiner queryParams = new StringJoiner("&");

            if(region != null) {
                encodedRegion = URLEncoder.encode(region, StandardCharsets.UTF_8.toString());
                queryParams.add("name=" + encodedRegion);
            }

            if(market != null) {
                encodedMarket = URLEncoder.encode(market, StandardCharsets.UTF_8.toString());
                queryParams.add("transaction=" + encodedMarket);
            }

            if(type != null) {
                encodedType = URLEncoder.encode(type, StandardCharsets.UTF_8.toString());
                queryParams.add("surface=" + encodedType);
            }

            MarketUrl = "http://localhost:8080/api/housingPrices/?" + queryParams.toString();

        }

        System.out.println(MarketUrl);

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

    public static void addHousingPrice(String region, String market, String type, String price) throws Exception, RuntimeException {
        try {

            checkForm(region, market, type, price);

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
        } catch (Exception e) {
            throw new Exception("Failed to add housing price: " + e.getMessage(), e);
        }

    }

    public static Integer getLastYear(String region, String market, String type) throws Exception {

        try{
            JSONArray combinedResponse = new JSONArray();
            combinedResponse = fetchHousing(region, market, type, combinedResponse);
            return combinedResponse.getJSONObject(combinedResponse.length() - 1).getInt("year");
        } catch (Exception e) {
            throw new Exception("Failed to get last year.", e);
        }

    }

    public static void checkForm(String region, String market, String type, String price) {
        if (region != null && !region.equals("Choose Region") &&
                market != null && !market.equals("Choose Market") &&
                type != null && !type.equals("Choose Type") &&
                price != null && !price.isBlank()) {
                Integer.parseInt(price);
//            try {
//                Integer.parseInt(price);
//            } catch (NumberFormatException e) {
//                System.out.println("RZUCAM");
//                throw new IllegalArgumentException("Invalid price format: " + price);
//            }
        } else {
            throw new IllegalArgumentException("One or more fields have default values or are empty.");
        }
    }

    public static void updateHousingRecord(HousingPricesModel record) throws Exception {
        try{
            checkForm(record.getName(), record.getTransaction(), record.getSurface(), record.getPrice().toString());
            String urlString = "http://localhost:8080/api/housingPrices/" + record.getId();
            System.out.println(urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String token = AuthManager.getToken();
            if (token != null && !token.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }


            JSONObject json = new JSONObject();
            json.put("name", record.getName());
            json.put("transaction", record.getTransaction());
            json.put("surface", record.getSurface());
            json.put("year", record.getYear());
            json.put("price", record.getPrice());

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            catch(Exception e) {
                throw new Exception("Failed to update housing record:" + e.getMessage(), e);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Failed to update record: HTTP response code " + responseCode);
            }
        }catch (Exception e) {

            System.out.println("Failed to update record: " + e.getMessage());
            throw new Exception("Failed to update " + e.getMessage(), e);

        }

    }

    public static void deleteHousingRecord(int id) throws Exception {
        String urlString  = "http://localhost:8080/api/housingPrices/" + id;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        String token = AuthManager.getToken();
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to delete record: HTTP response code " + responseCode);
        }
    }


}

