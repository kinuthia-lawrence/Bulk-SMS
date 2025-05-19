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
     * Sends an SMS message to a predefined mobile number using the MobiTech API.
     * The message contains hardcoded values for recipient, sender name, and content.
     *
     * @throws RuntimeException if any error occurs during the API request process
     */
    @Override
    public void sendSms() {
        try {
            String payload = """
                        {
                            "mobile": "+254710986455",
                            "response_type": "json",
                            "sender_name": "COOP_UNI",
                            "service_id": 0,
                            "message": "This is a message.\\n\\nRegards\\nLarrykin343 Technologies"
                        }
                    """;

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

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("SMS sent successfully.");
            } else {
                System.out.println("Failed to send SMS. Response Code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while sending the SMS.");
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
            URL url = new URL(API_URL_VERIFY + "?mobile=" + mobileNumber);
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
                System.out.println("Failed to verify mobile number. Response Code: " + responseCode);
                return "Verification failed with response code: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while verifying the mobile number.";
        }
    }
}