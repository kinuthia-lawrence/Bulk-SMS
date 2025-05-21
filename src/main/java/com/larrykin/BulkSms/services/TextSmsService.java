package com.larrykin.BulkSms.services;

/**
 * Service interface for TextSMS API integration.
 * Provides methods for sending SMS messages (single and bulk), scheduling messages,
 * retrieving delivery reports, and checking account balance.
 */
public interface TextSmsService {
    /**
     * Sends a single SMS message to the specified mobile number.
     *
     * @param mobile  The recipient mobile number
     * @param message The message content to send
     * @return JSON response from the API with the status of the message
     */
    String sendSingleSms(String mobile, String message);

    /**
     * Schedules an SMS message to be sent at a future time.
     *
     * @param mobile     The recipient mobile number
     * @param message    The message content to send
     * @param timeToSend The date and time to send the message (format: YYYY-MM-DD HH:MM or Unix timestamp)
     * @return JSON response from the API with the status of the scheduled message
     */
    String scheduleSms(String mobile, String message, String timeToSend);

    /**
     * Sends bulk SMS messages to multiple recipients with different message content.
     *
     * @param mobiles      Array of recipient mobile numbers
     * @param messages     Array of message contents corresponding to each recipient
     * @param clientSmsIds Array of client-defined SMS IDs for tracking purposes
     * @return JSON response from the API with the status for each message
     */
    String sendBulkSms(String[] mobiles, String[] messages, String[] clientSmsIds);

    /**
     * Retrieves the delivery report for a specific message.
     *
     * @param messageId The ID of the message to get the delivery report for
     * @return JSON response containing the delivery status information
     */
    String getDeliveryReport(String messageId);

    /**
     * Retrieves the current account balance.
     *
     * @return JSON response containing account balance information
     */
    String getAccountBalance();
}