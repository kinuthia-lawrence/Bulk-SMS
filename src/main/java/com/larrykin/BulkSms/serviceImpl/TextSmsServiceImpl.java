package com.larrykin.BulkSms.serviceImpl;

import com.larrykin.BulkSms.services.TextSmsService;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of the TextSmsService interface for TextSMS API integration.
 * Provides functionality for sending SMS messages, scheduling messages,
 * retrieving delivery reports, and checking account balance.
 */
@Service
public class TextSmsServiceImpl implements TextSmsService {

    private final Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("TEXTSMS_API_KEY");
    private final String PARTNER_ID = dotenv.get("TEXTSMS_PARTNER_ID");
    private final String SENDER_ID = dotenv.get("TEXTSMS_SENDER_ID");
    private final String GET_URL = dotenv.get("TEXTSMS_GET_URL");
    private final String POST_URL = dotenv.get("TEXTSMS_POST_URL");
    private final String BULK_URL = dotenv.get("TEXTSMS_BULK_URL");
    private final String DLR_URL = dotenv.get("TEXTSMS_DLR_URL");
    private final String BALANCE_URL = dotenv.get("TEXTSMS_BALANCE_URL");

    /**
     * {@inheritDoc}
     * Sends a single SMS message to the specified mobile number using TextSMS API.
     * Formats the mobile number to ensure compatibility with the API requirements.
     *
     * @throws Exception if an error occurs during the API request
     */
    @Override
    public String sendSingleSms(String mobile, String message) {
        try {
            // Format mobile number if needed
            String formattedMobile = formatMobileNumber(mobile);

            JSONObject requestBody = new JSONObject();
            requestBody.put("apikey", API_KEY);
            requestBody.put("partnerID", PARTNER_ID);
            requestBody.put("message", message);
            requestBody.put("shortcode", SENDER_ID);
            requestBody.put("mobile", formattedMobile);

            return executePostRequest(POST_URL, requestBody.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"responses\":[{\"respose-code\":\"9999\",\"response-description\":\"Error: " +
                    e.getMessage().replace("\"", "\\\"") + "\"}]}";
        }
    }

    /**
     * {@inheritDoc}
     * Schedules an SMS message to be sent at a future time using TextSMS API.
     * Formats the mobile number to ensure compatibility with the API requirements.
     *
     * @throws Exception if an error occurs during the API request
     */
    @Override
    public String scheduleSms(String mobile, String message, String timeToSend) {
        try {
            String formattedMobile = formatMobileNumber(mobile);

            JSONObject requestBody = new JSONObject();
            requestBody.put("apikey", API_KEY);
            requestBody.put("partnerID", PARTNER_ID);
            requestBody.put("message", message);
            requestBody.put("shortcode", SENDER_ID);
            requestBody.put("mobile", formattedMobile);
            requestBody.put("timeToSend", timeToSend);

            return executePostRequest(POST_URL, requestBody.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"responses\":[{\"respose-code\":\"9999\",\"response-description\":\"Error: " +
                    e.getMessage().replace("\"", "\\\"") + "\"}]}";
        }
    }

    /**
     * {@inheritDoc}
     * Sends bulk SMS messages to multiple recipients using TextSMS API.
     * Formats each mobile number to ensure compatibility with the API requirements.
     *
     * @throws IllegalArgumentException if the input arrays are not of the same length
     * @throws Exception                if an error occurs during the API request
     */
    @Override
    public String sendBulkSms(String[] mobiles, String[] messages, String[] clientSmsIds) {
        try {
            if (mobiles.length != messages.length || mobiles.length != clientSmsIds.length) {
                throw new IllegalArgumentException("Arrays must be of same length");
            }

            JSONObject requestBody = new JSONObject();
            requestBody.put("count", mobiles.length);

            JSONArray smsList = new JSONArray();
            for (int i = 0; i < mobiles.length; i++) {
                JSONObject sms = new JSONObject();
                sms.put("partnerID", PARTNER_ID);
                sms.put("apikey", API_KEY);
                sms.put("pass_type", "plain");
                sms.put("clientsmsid", clientSmsIds[i]);
                sms.put("mobile", formatMobileNumber(mobiles[i]));
                sms.put("message", messages[i]);
                sms.put("shortcode", SENDER_ID);
                smsList.put(sms);
            }
            requestBody.put("smslist", smsList);

            return executePostRequest(BULK_URL, requestBody.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"responses\":[{\"respose-code\":\"9999\",\"response-description\":\"Error: " +
                    e.getMessage().replace("\"", "\\\"") + "\"}]}";
        }
    }

    /**
     * {@inheritDoc}
     * Retrieves the delivery report for a specific message using TextSMS API.
     *
     * @throws Exception if an error occurs during the API request
     */
    @Override
    public String getDeliveryReport(String messageId) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("apikey", API_KEY);
            requestBody.put("partnerID", PARTNER_ID);
            requestBody.put("messageID", messageId);

            return executePostRequest(DLR_URL, requestBody.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"respose-code\":\"9999\",\"response-description\":\"Error: " +
                    e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }

    /**
     * {@inheritDoc}
     * Retrieves the current account balance using TextSMS API.
     *
     * @throws Exception if an error occurs during the API request
     */
    @Override
    public String getAccountBalance() {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("apikey", API_KEY);
            requestBody.put("partnerID", PARTNER_ID);

            return executePostRequest(BALANCE_URL, requestBody.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"respose-code\":\"9999\",\"response-description\":\"Error: " +
                    e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }

    /**
     * Formats a mobile number to ensure compatibility with the TextSMS API requirements.
     * - Removes any leading '+' sign
     * - Converts numbers starting with '0' to start with '254' (Kenya country code)
     * - Adds '254' prefix to any number without a country code
     *
     * @param mobile The mobile number to format
     * @return The formatted mobile number
     */
    private String formatMobileNumber(String mobile) {
        // Remove any leading + sign
        if (mobile.startsWith("+")) {
            mobile = mobile.substring(1);
        }

        // If starts with 0, replace with 254
        if (mobile.startsWith("0")) {
            mobile = "254" + mobile.substring(1);
        }

        // If doesn't have country code, add 254
        if (mobile.length() <= 10) {
            mobile = "254" + mobile;
        }

        return mobile;
    }

    /**
     * Executes a POST request to the specified URL with the given JSON input.
     *
     * @param urlString       The URL to send the request to
     * @param jsonInputString The JSON input string to include in the request body
     * @return The response from the server as a string
     * @throws Exception if an error occurs during the HTTP request
     */
    private String executePostRequest(String urlString, String jsonInputString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        BufferedReader in;
        if (responseCode >= 200 && responseCode < 300) {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    /**
     * Executes a GET request to the specified URL with parameters for sending an SMS.
     *
     * @param baseUrl The base URL to send the request to
     * @param mobile  The recipient mobile number
     * @param message The message content to send
     * @return The response from the server as a string
     * @throws Exception if an error occurs during the HTTP request
     */
    private String executeGetRequest(String baseUrl, String mobile, String message) throws Exception {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        String urlString = baseUrl +
                "apikey=" + API_KEY +
                "&partnerID=" + PARTNER_ID +
                "&message=" + encodedMessage +
                "&shortcode=" + SENDER_ID +
                "&mobile=" + formatMobileNumber(mobile);

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}