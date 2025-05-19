package com.larrykin.BulkSms.services;

/**
 * Service interface for MobiTech SMS operations.
 * Provides functionality for sending SMS messages and verifying mobile numbers
 * through the MobiTech Technologies API.
 */
public interface MobiTechSmsService {

    /**
     * Sends an SMS message using the MobiTech API.
     * Implementation typically handles the API connection and payload formatting.
     */
    void sendSms();

    /**
     * Verifies if a given mobile number is valid using the MobiTech API.
     *
     * @param mobileNumber the mobile number to verify, should be in international format
     * @return JSON response from the API containing verification results or an error message
     */
    String verifyMobileNumber(String mobileNumber);
}