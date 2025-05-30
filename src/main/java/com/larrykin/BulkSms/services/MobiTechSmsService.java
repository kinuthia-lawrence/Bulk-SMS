package com.larrykin.BulkSms.services;

/**
 * Service interface for MobiTech SMS operations.
 * Provides functionality for sending SMS messages and verifying mobile numbers
 * through the MobiTech Technologies API.
 */
public interface MobiTechSmsService {

    /**
     * Sends an SMS message using the MobiTech API.
     *
     * @return JSON response from the API containing status and message details
     */
    String sendSms();

    /**
     * Verifies if a given mobile number is valid using the MobiTech API.
     *
     * @param mobileNumber the mobile number to verify, should be in international format
     * @return JSON response from the API containing verification results or an error message
     */
    String verifyMobileNumber(String mobileNumber);


    /**
     * Sends SMS messages to multiple recipients.
     *
     * @param mobileNumbers Array of mobile numbers to send messages to
     * @param message Message content to send (if null, uses default message)
     * @return JSON array with response for each recipient
     */
    String sendBulkSms(String[] mobileNumbers, String message);
}