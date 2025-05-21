package com.larrykin.BulkSms.controllers;

import com.larrykin.BulkSms.services.MobiTechSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for MobiTech SMS operations.
 * Provides endpoints for sending SMS messages and verifying mobile numbers
 * through the MobiTech Technologies API.
 */
@RestController
@RequestMapping("/api/sms/mobitech")
public class MobiTechSmsController {
    @Autowired
    private MobiTechSmsService mobiTechSmsService;

    /**
     * Endpoint to send an SMS message.
     * Triggers the SMS sending process through the MobiTech service.
     *
     * @return a string indicating the result of the SMS sending operation
     */
    @PostMapping("/send")
    public String sendSms() {

        return mobiTechSmsService.sendSms();
    }

    /**
     * Endpoint to verify if a mobile number is valid.
     * Passes the mobile number to the MobiTech service for verification.
     *
     * @param mobile the mobile number to verify, should be in international format
     * @return JSON response from the API containing verification results or an error message
     */
    @GetMapping("/verify")
    public String verifyMobileNumber(@RequestParam String mobile) {
        return mobiTechSmsService.verifyMobileNumber(mobile);
    }

    /**
     * Endpoint to send bulk SMS messages to multiple recipients.
     *
     * @param recipients Array of recipient phone numbers
     * @param message Optional message content (uses default if not provided)
     * @return JSON array with status for each recipient
     */
    @PostMapping("/bulk")
    public String sendBulkSms(@RequestParam String[] recipients,
                             @RequestParam(required = false) String message) {
        return mobiTechSmsService.sendBulkSms(recipients, message);
    }
}