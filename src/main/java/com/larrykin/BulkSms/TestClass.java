package com.larrykin.BulkSms;

import com.larrykin.BulkSms.services.TwilioSmsService;

public class TestClass {
    public static void main(String[] args) {
        TwilioSmsService twilioSmsService = new TwilioSmsService();
        twilioSmsService.sendSms();
    }
}
