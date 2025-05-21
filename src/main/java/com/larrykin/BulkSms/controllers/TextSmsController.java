package com.larrykin.BulkSms.controllers;

import com.larrykin.BulkSms.services.TextSmsService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for TextSMS API integration.
 * Provides endpoints for sending single and bulk SMS messages, scheduling messages,
 * retrieving delivery reports, and checking account balance.
 */
@RestController
@RequestMapping("/api/sms/textsms")
public class TextSmsController {

    @Autowired
    private TextSmsService textSmsService;

    /**
     * Sends a single SMS message to the specified mobile number.
     *
     * @param mobile  The recipient mobile number
     * @param message The message content to send
     * @return JSON response from the API with the status of the message
     */
    @PostMapping("/send")
    public String sendSingleSms(@RequestParam String mobile, @RequestParam String message) {
        return textSmsService.sendSingleSms(mobile, message);
    }

    /**
     * Schedules an SMS message to be sent at a future time.
     *
     * @param mobile     The recipient mobile number
     * @param message    The message content to send
     * @param timeToSend The date and time to send the message (format: YYYY-MM-DD HH:MM or Unix timestamp)
     * @return JSON response from the API with the status of the scheduled message
     */
    @PostMapping("/schedule")
    public String scheduleSms(
            @RequestParam String mobile,
            @RequestParam String message,
            @RequestParam String timeToSend) {
        return textSmsService.scheduleSms(mobile, message, timeToSend);
    }

    /**
     * Sends bulk SMS messages to multiple recipients with different message content.
     *
     * @param requestBody A map containing:
     *                    - mobiles: Comma-separated list of mobile numbers
     *                    - messages: Pipe-separated list of messages
     *                    - clientSmsIds: Comma-separated list of client SMS IDs
     * @return JSON response from the API with the status for each message
     */
    @PostMapping("/bulk")
    public String sendBulkSms(@RequestBody Map<String, Object> requestBody) {
        String[] mobiles = ((String) requestBody.get("mobiles")).split(",");
        String[] messages = ((String) requestBody.get("messages")).split("\\|");
        String[] clientSmsIds = ((String) requestBody.get("clientSmsIds")).split(",");

        return textSmsService.sendBulkSms(mobiles, messages, clientSmsIds);
    }

    /**
     * Retrieves the delivery report for a specific message.
     *
     * @param messageId The ID of the message to get the delivery report for
     * @return JSON response containing the delivery status information
     */
    @GetMapping("/dlr/{messageId}")
    public String getDeliveryReport(@PathVariable String messageId) {
        return textSmsService.getDeliveryReport(messageId);
    }

    /**
     * Retrieves the current account balance.
     *
     * @return JSON response containing account balance information
     */
    @GetMapping("/balance")
    public String getAccountBalance() {
        return textSmsService.getAccountBalance();
    }

    /**
     * Sends the same SMS message to all recipients defined in the BULK_SMS_RECIPIENTS environment variable.
     * Uses system timestamp to generate unique client SMS IDs for each recipient.
     *
     * @param message The message content to send to all recipients
     * @return JSON response from the API with the status for each message
     */
    @PostMapping("/send-default-bulk")
    public String sendBulkSmsToDefaultRecipients(@RequestParam String message) {
        // Use environment variable BULK_SMS_RECIPIENTS
        Dotenv dotenv = Dotenv.load();
        String recipients = dotenv.get("BULK_SMS_RECIPIENTS");
        String[] mobiles = recipients.split(",");

        // Create messages and IDs arrays of the same length
        String[] messages = new String[mobiles.length];
        String[] clientSmsIds = new String[mobiles.length];

        for (int i = 0; i < mobiles.length; i++) {
            messages[i] = message;
            clientSmsIds[i] = String.valueOf(System.currentTimeMillis() + i);
        }

        return textSmsService.sendBulkSms(mobiles, messages, clientSmsIds);
    }
}