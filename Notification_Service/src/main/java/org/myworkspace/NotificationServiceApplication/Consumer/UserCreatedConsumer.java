package org.myworkspace.NotificationServiceApplication.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.myworkspace.Utilities.CommonConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    @KafkaListener(topics = {CommonConstraints.USER_CREATED_TOPIC}, groupId = "notification-group")
    public void sendNotification(String msg) throws JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(msg, JSONObject.class);
        String name = (String) jsonObject.get(CommonConstraints.USER_NAME);
        String email = (String) jsonObject.get(CommonConstraints.USER_EMAIL);

        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom("e-wallet@gmail.com");
        simpleMailMessage.setSubject("Welcome Mailer");
        simpleMailMessage.setText("Welcome "+ name + "\nYour E-wallet has been created!!");

        sender.send(simpleMailMessage);
        logger.info("Mail has been sent to "+ email);
    }
}
