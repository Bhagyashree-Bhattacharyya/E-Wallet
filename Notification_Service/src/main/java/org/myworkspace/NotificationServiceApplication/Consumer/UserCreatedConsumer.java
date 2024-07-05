package org.myworkspace.NotificationServiceApplication.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.json.simple.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import org.myworkspace.Utilities.CommonConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class UserCreatedConsumer {

    private static Logger logger = LoggerFactory.getLogger(UserCreatedConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SimpleMailMessage simpleMailMessage;
    @Autowired
    private JavaMailSender sender;

    @Value("${user.creation.time.amount.credited}")
    private double amount;


    @KafkaListener(topics = {CommonConstraints.USER_CREATED_TOPIC, CommonConstraints.WALLET_CREATED_TOPIC}, groupId = "notification-group")
    public void consume(String msg, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            if (topic.equals(CommonConstraints.USER_CREATED_TOPIC)) {
                handleUserCreated(msg);
            } else if (topic.equals(CommonConstraints.WALLET_CREATED_TOPIC)) {
                handleWalletCreated(msg);
            }
        } catch (Exception e) {
            logger.error("Unexpected error processing message from topic {}: {}", topic, msg, e);
        }
    }


    private void handleUserCreated(String msg) {
        try {
            JsonNode jsonNode = objectMapper.readTree(msg);
            String name = jsonNode.get(CommonConstraints.USER_NAME).asText();
            String email = jsonNode.get(CommonConstraints.USER_EMAIL).asText();

            simpleMailMessage.setTo(email);
            simpleMailMessage.setFrom("e-wallet@gmail.com");
            simpleMailMessage.setSubject("Welcome Mailer");
            simpleMailMessage.setText("Welcome " + name + "\nYour E-wallet has been created!!");

            sender.send(simpleMailMessage);
            logger.info("Mail has been sent to " + email);
        } catch (Exception e) {
            logger.error("Error handling user creation message: {}", msg, e);
        }
    }

    private void handleWalletCreated(String msg) {
        try {
            JsonNode jsonNode = objectMapper.readTree(msg);

            // Check if the required fields exist
            if (jsonNode.has(CommonConstraints.USER_ID) &&
                    jsonNode.has(CommonConstraints.USER_NAME) &&
                    jsonNode.has(CommonConstraints.USER_EMAIL) &&
                    jsonNode.has(CommonConstraints.WALLET_BALANCE)) {

                // Retrieve values if present
                Integer userId = jsonNode.get(CommonConstraints.USER_ID).asInt();
                String name = jsonNode.get(CommonConstraints.USER_NAME).asText();
                String email = jsonNode.get(CommonConstraints.USER_EMAIL).asText();
                Double balance = jsonNode.get(CommonConstraints.WALLET_BALANCE).asDouble();

                simpleMailMessage.setTo(email);
                simpleMailMessage.setFrom("e-wallet@gmail.com");
                simpleMailMessage.setSubject("Amount Credited");
                simpleMailMessage.setText("Hi " + name + "\nYour E-wallet has been credited with Rs." + amount + " !!\n Go start transaction !!");

                sender.send(simpleMailMessage);
                logger.info("Mail has been sent to " + email);

            } else {
                logger.error("Missing or incomplete data in wallet creation message: {}", msg);
            }
        } catch (Exception e) {
            logger.error("Error handling wallet creation message: {}", msg, e);
        }
    }


//    @KafkaListener(topics = {CommonConstraints.USER_CREATED_TOPIC}, groupId = "notification-group")
//    public void sendNotification(String msg) throws JsonProcessingException {
//        JSONObject jsonObject = objectMapper.readValue(msg, JSONObject.class);
//        String name = (String) jsonObject.get(CommonConstraints.USER_NAME);
//        String email = (String) jsonObject.get(CommonConstraints.USER_EMAIL);
//
//        simpleMailMessage.setTo(email);
//        simpleMailMessage.setFrom("e-wallet@gmail.com");
//        simpleMailMessage.setSubject("Welcome Mailer");
//        simpleMailMessage.setText("Welcome "+ name + "\nYour E-wallet has been created!!");
//
//        sender.send(simpleMailMessage);
//        logger.info("Mail has been sent to "+ email);
//    }
}
