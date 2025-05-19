# MobiTech SMS API Documentation

## Overview
This documentation provides information on how to integrate with the MobiTech Bulk SMS and Short code / Premium messaging gateway through their API.

## Authentication
To use this API, you need an API Key. If you have already signed up, you can get your API Key from your account.

## API Endpoints

### 1. Send SMS
Send messages using bulk or shortcode services.

**Endpoint:** `https://api.mobitechtechnologies.com/sms/sendsms`  
**Method:** `POST`

#### Request Parameters

| Field | Type | Description |
|-------|------|-------------|
| h_api_key | String | Your API key (included in header) |
| mobile | String | Customer mobile number (any format: 722xxxyyy, 0722xxxyyy, +254 722xxx yyy) |
| response_type | String | [Optional, defaults to json] either "json" or "plain" |
| sender_name | String | Origination alphanumeric/numeric code (e.g., "COOP_UNI" for bulk, "12345" for shortcode) |
| service_id | Integer | Service identifier (always 0 for bulk messaging) |
| link_id | String | [Optional] Empty for bulk messages. For shortcode messages, include link_id from incoming on-demand messages |
| message | String | Message content (max 920 characters or 6 SMS units) |

#### Example Request (cURL)
```bash
curl --location --request POST 'https://api.mobitechtechnologies.com/sms/sendsms' \
--header 'h_api_key: b39d802ddfa4f63ad2f51602b8118f5e34a2e7d4a25906b5' \
--header 'Content-Type: application/json' \
--data-raw '{
    "mobile": "+254710986455",
    "response_type": "json",
    "sender_name": "COOP_UNI",
    "service_id": 0,
    "message": "This is a message.\n\nRegards\nMobitech Technologies"
}'
```

### 2. Validate Mobile Number
Validate and check the network a mobile number belongs to.

**Endpoint:** `https://api.mobitechtechnologies.com/sms/mobile`  
**Method:** `GET`

#### Request Parameters

| Field | Type | Description |
|-------|------|-------------|
| h_api_key | String | Your API key (included in header) |
| return | String | Return type: either "json" or just the validated mobile number |
| mobile | String | The mobile number to be validated |

#### Example Request (cURL)
```bash
curl --location --request GET 'https://api.mobitechtechnologies.com/sms/mobile?mobile=710986455' \
--header 'h_api_key: b39d802ddfa4f63ad2f51602b8118f5e34a2e7d4a25906b5'
```

## Response Status Codes

| Status Id | Status Code | Status Description |
|-----------|-------------|-------------------|
| 1 | 1000 | Success |
| 2 | 1001 | Invalid short code |
| 3 | 1002 | Network not allowed |
| 4 | 1003 | Invalid mobile number |
| 5 | 1004 | Low bulk credits |
| 6 | 1005 | Internal system error |
| 7 | 1006 | Invalid credentials |
| 8 | 1007 | Db connection failed |
| 9 | 1008 | Db selection failed |
| 10 | 1009 | Data type not supported |
| 11 | 1010 | Request type not supported |
| 12 | 1011 | Invalid user state or account suspended |
| 13 | 1012 | Mobile number in DND |
| 14 | 1013 | Invalid API Key |
| 15 | 1014 | IP not allowed |

## Sample Responses

### Success Response
```json
[
  {
    "status_code": "1000",
    "status_desc": "Success",
    "message_id": 7831698,
    "mobile_number": "+254710986455",
    "network_id": "1",
    "message_cost": "0.75",
    "credit_balance": "3262"
  }
]
```

### Error Response
```json
[
  {
    "status_code": "1001",
    "status_desc": "Invalid short code",
    "message_id": "0",
    "mobile_number": "+254710986455",
    "network_id": "",
    "message_cost": "",
    "credit_balance": ""
  }
]
```

## Notes
- Each unit of an SMS is charged separately
- The maximum message length is 920 characters or 6 SMS units
- You can import the cURL examples into Postman to generate sample code in different languages