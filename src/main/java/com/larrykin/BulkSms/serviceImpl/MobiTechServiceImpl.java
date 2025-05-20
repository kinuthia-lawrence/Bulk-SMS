package com.larrykin.BulkSms.serviceImpl;

import com.larrykin.BulkSms.services.MobiTechSmsService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of the MobiTechSmsService interface for sending bulk SMS and verifying mobile numbers.
 * This service communicates with the MobiTech API to send messages and verify phone numbers.
 */
@Service
public class MobiTechServiceImpl implements MobiTechSmsService {

    private final Dotenv dotenv = Dotenv.load();
    private final String API_URL_SMS = dotenv.get("MOBITECH_API_URL_SMS");
    private final String API_URL_VERIFY = dotenv.get("MOBITECH_API_URL_VERIFY");
    private final String API_KEY = dotenv.get("MOBITECH_API_KEY");


    /**
     * /**
     * Sends an SMS message and returns the API response.
     *
     * @return JSON response containing status code, message ID, and other details
     */
    @Override
    public String sendSms() {
        try {
            // Use a sender name from environment variables instead of hardcoding
            String senderName = dotenv.get("SENDER_NAME");

            String payload = String.format("""
                    {
                        "mobile": "%s",
                        "response_type": "json",
                        "sender_name": "%s",
                        "service_id": 0,
                        "message": "This is a message.\\n\\nRegards\\nLarrykin343 Technologies"
                    }
                    """, dotenv.get("RECIPIENT_PHONE_NUMBER"), senderName);

            URL url = new URL(API_URL_SMS);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("h_api_key", API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read response regardless of success or failure
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(responseCode == HttpURLConnection.HTTP_OK ?
                            connection.getInputStream() : connection.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response: " + response);
                return response.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return String.format("[{\"status_code\":\"9999\",\"status_desc\":\"Error: %s\"}]",
                    e.getMessage().replace("\"", "\\\""));
        }
    }

    /**
     * Verifies if a given mobile number is valid using the MobiTech API.
     * Makes a GET request to the verification endpoint with the mobile number as a parameter.
     *
     * @param mobileNumber the mobile number to verify, should be in international format
     * @return JSON response from the API containing verification results or an error message
     */
    @Override
    public String verifyMobileNumber(String mobileNumber) {
        try {
            // Format mobile number according to API examples (remove + and possibly strip country code)
            String formattedNumber = mobileNumber.replace("+", "");
            if (formattedNumber.startsWith("254")) {
                // Remove country code if API expects only local number (based on examples)
                formattedNumber = formattedNumber.substring(3);
            }

            // Add required return=json parameter
            URL url = new URL(API_URL_VERIFY + "?return=json&mobile=" + formattedNumber);
            System.out.println("Requesting URL: " + url); // Debug the final URL

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("h_api_key", API_KEY);

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Response: " + response);
                    return response.toString();
                }
            } else {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }
                    System.out.println("Error Response: " + errorResponse);
                    return "Verification failed with response code: " + responseCode + " - " + errorResponse;
                } catch (Exception e) {
                    return "Verification failed with response code: " + responseCode;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while verifying the mobile number: " + e.getMessage();
        }
    }


    /**
     * Sends SMS messages to multiple recipients.
     *
     * @param mobileNumbers Array of mobile numbers to send messages to
     * @param message Message content to send (if null, uses default message)
     * @return JSON array with response for each recipient
     */
    @Override
    public String sendBulkSms(String[] mobileNumbers, String message) {
        if (mobileNumbers == null || mobileNumbers.length == 0) {
            return "[{\"status_code\":\"9999\",\"status_desc\":\"Error: No recipients provided\"}]";
        }

        StringBuilder allResponses = new StringBuilder("[");
        String defaultMessage = "This is a message.\\n\\nRegards\\nLarrykin343 Technologies";
        String textToSend = (message != null && !message.isEmpty()) ? message : defaultMessage;
        String senderName = dotenv.get("SENDER_NAME");

        try {
            for (int i = 0; i < mobileNumbers.length; i++) {
                if (mobileNumbers[i] == null || mobileNumbers[i].trim().isEmpty()) {
                    continue;
                }

                String payload = String.format("""
                        {
                            "mobile": "%s",
                            "response_type": "json",
                            "sender_name": "%s",
                            "service_id": 0,
                            "message": "%s"
                        }
                        """, mobileNumbers[i].trim(), senderName, textToSend);

                URL url = new URL(API_URL_SMS);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("h_api_key", API_KEY);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = payload.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Read response
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    // Remove the brackets from individual responses for clean array formatting
                    String formattedResponse = response.toString().replace("[", "").replace("]", "");

                    // Add to collection of responses
                    if (i > 0) allResponses.append(",");
                    allResponses.append(formattedResponse);
                } catch (Exception e) {
                    if (i > 0) allResponses.append(",");
                    allResponses.append(String.format("{\"status_code\":\"9999\",\"status_desc\":\"Error for %s: %s\"}",
                            mobileNumbers[i], e.getMessage().replace("\"", "\\\"")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return String.format("[{\"status_code\":\"9999\",\"status_desc\":\"Bulk send error: %s\"}]",
                    e.getMessage().replace("\"", "\\\""));
        }

        allResponses.append("]");
        return allResponses.toString();
    }
}