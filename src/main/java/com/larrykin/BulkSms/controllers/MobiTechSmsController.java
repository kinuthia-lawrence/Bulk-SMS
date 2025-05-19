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
@RequestMapping("/api/sms")
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
        mobiTechSmsService.sendSms();
        return "SMS sent successfully.";
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
}