package org.myworkspace.WalletServiceApplication.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.protocol.types.Field;
import org.json.simple.JSONObject;
import org.myworkspace.WalletServiceApplication.Entities.Wallet;
import org.myworkspace.WalletServiceApplication.Repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.myworkspace.Utilities.CommonConstraints;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserCreatedConsumer {

    private static Logger logger = LoggerFactory.getLogger(UserCreatedConsumer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WalletRepository walletRepository;

    @Value("${user.creation.time.balance}")
    private double balance;

    @KafkaListener(topics = {CommonConstraints.USER_CREATED_TOPIC}, groupId = "wallet-group")
    public void createWallet(String msg) throws JsonProcessingException {
        JSONObject object = objectMapper.readValue(msg, JSONObject.class);
        Integer userId = (Integer) object.get(CommonConstraints.USER_ID);
        String contactNo = (String) object.get(CommonConstraints.USER_CONTACT_NO);

        Wallet wallet = Wallet.builder().contactNo(contactNo).userId(userId).balance(balance).build();
        walletRepository.save(wallet);

        logger.info("wallet has been created for the user");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonConstraints.USER_ID, userId);
        jsonObject.put(CommonConstraints.WALLET_BALANCE, balance);
        kafkaTemplate.send(CommonConstraints.WALLET_CREATED_TOPIC, objectMapper.writeValueAsString(jsonObject));

        logger.info("produced wallet creation msg in the queue for user id" + userId);
    }
}
