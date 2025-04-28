package tn.esprit.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioService {
    // Your Twilio Account SID and Auth Token
    public static final String ACCOUNT_SID = "ACb32ad83e8facf08090def0b1c917ef9b";
    public static final String AUTH_TOKEN = "cffdca61208c7a30342e9b46a8c60b15";
    public static final String FROM_PHONE_NUMBER = "+14134811751";
    // Twilio phone number
    public static final String TO_PHONE_NUMBER = "+21699407100";  // Your Twilio number

    public static void sendSMS(String messageBody) {
        // Initialize Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Send the SMS
        Message message = Message.creator(
                new PhoneNumber(TO_PHONE_NUMBER), // Recipient's phone number
                new PhoneNumber(FROM_PHONE_NUMBER), // Twilio phone number
                messageBody // SMS content
        ).create();

        // Output the SID of the sent message (for debugging)
        System.out.println("Message sent with SID: " + message.getSid());
    }
}
