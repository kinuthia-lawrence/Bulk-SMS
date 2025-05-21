# TextSMS Bulk SMS API Documentation

## Introduction

With the TextSMS Bulk SMS API, you can integrate your systems to automatically leverage the instant delivery of SMS to generate receipts, alerts, and relevant communications to your clients and customers.

## API Methods

### GET Method

**URL:** `https://sms.textsms.co.ke/api/services/sendsms/?`

**GET Parameters:**
- `apikey` - Valid API KEY (Get this from "GET API KEY & PARTNER ID" button in your account)
- `partnerID` - Valid Partner ID (Get this from "GET API KEY & PARTNER ID" button)
- `message` - URL Encoded Text Message with valid GSM7 Characters
- `shortcode` - Valid Sender ID / Shortcode
- `mobile` - Valid Mobile Number

### POST Method

**REQUEST ENDPOINT:** `https://sms.textsms.co.ke/api/services/sendsms/`

**Sample Request Body:**
```json
{
  "apikey": "123456789",
  "partnerID": "123",
  "message": "this is a test message",
  "shortcode": "SENDERID",
  "mobile": "254712345678"
}
```

**Sample Response:**
```json
{
  "responses": [
    {
      "respose-code": 200,
      "response-description": "Success",
      "mobile": 254712345678,
      "messageid": 8290842,
      "networkid": "1"
    }
  ]
}
```

## Scheduling Messages

For messages to be sent at a future time, add the optional parameter `timeToSend` with a valid date string or Unix timestamp.

**Sample Request Body:**
```json
{
  "apikey": "123456789",
  "partnerID": "123",
  "message": "this is a test message",
  "shortcode": "SENDERID",
  "mobile": "254712345678",
  "timeToSend": "2019-09-01 18:00"
}
```

## Sending Bulk Messages

Send up to 20 bulk messages in one single call using this endpoint.

**API URL ENDPOINT:** `https://sms.textsms.co.ke/api/services/sendbulk/`

**Sample Request Body:**
```json
{
  "count": 3,
  "smslist": [
    {
      "partnerID": "12345",
      "apikey": "6565b5a73b8221",
      "pass_type": "plain",
      "clientsmsid": 1234,
      "mobile": "0733123456",
      "message": "This is a test message 1",
      "shortcode": "TextSMS"
    },
    {
      "partnerID": "12346",
      "apikey": "75465b5a73b8221",
      "mobile": "0711123456",
      "clientsmsid": 1235,
      "message": "This is a test message 3",
      "shortcode": "TextSMS",
      "pass_type": "plain"
    },
    {
      "partnerID": "123457",
      "apikey": "sw23454t2xd24",
      "mobile": "0755123456",
      "clientsmsid": 1236,
      "message": "This is a test message 2",
      "shortcode": "TextSMS",
      "pass_type": "plain"
    }
  ]
}
```

**Sample Response:**
```json
{
  "responses": [
    {
      "respose-code": 200,
      "response-description": "Success",
      "mobile": "254733123456",
      "messageid": 75085465,
      "clientsmsid": "1234",
      "networkid": "2"
    },
    {
      "respose-code": 200,
      "response-description": "Success",
      "mobile": "254711123456",
      "messageid": 75085466,
      "clientsmsid": "1235",
      "networkid": "1"
    },
    {
      "respose-code": 1006,
      "response-description": "Invalid credentials",
      "mobile": "0755123456",
      "partnerID": "1",
      "shortcode": null,
      "clientsmsid": "1236"
    }
  ]
}
```

## Getting Delivery Reports

**ENDPOINT URL:** `https://sms.textsms.co.ke/api/services/getdlr/`

**Sample Request Body:**
```json
{
  "apikey": "123456789",
  "partnerID": "123",
  "messageID": "123456789"
}
```

## Getting Account Balance

**ENDPOINT URL:** `https://sms.textsms.co.ke/api/services/getbalance/`

**Sample Request Body:**
```json
{
  "apikey": "123456789",
  "partnerID": "123"
}
```

## Response Codes

- `200` - Successful Request Call
- `1001` - Invalid sender id
- `1002` - Network not allowed
- `1003` - Invalid mobile number
- `1004` - Low bulk credits
- `1005` - Failed. System error
- `1006` - Invalid credentials
- `1007` - Failed. System error
- `1008` - No Delivery Report
- `1009` - Unsupported data type
- `1010` - Unsupported request type
- `4090` - Internal Error. Try again after 5 minutes
- `4091` - No Partner ID is Set
- `4092` - No API KEY Provided
- `4093` - Details Not Found