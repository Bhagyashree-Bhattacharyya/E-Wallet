package org.myworkspace.WalletServiceApplication.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.myworkspace.Utilities.CommonConstraints;
import org.myworkspace.WalletServiceApplication.Entities.Wallet;
import org.myworkspace.WalletServiceApplication.Repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TxnInitiatedConsumer {
//    @Value("${wallet-group-id}")
//    private String walletGroup;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @KafkaListener(topics = CommonConstraints.TXN_INITIATED_TOPIC, groupId = "wallet-group")
    public void updateWallet(String msg) throws JsonProcessingException {
        JSONObject object = objectMapper.readValue(msg, JSONObject.class);
        String sender = (String) object.get(CommonConstraints.SENDER);
        String receiver = (String) object.get(CommonConstraints.RECEIVER);
        Double amount = (Double) object.get(CommonConstraints.AMOUNT);
        String purpose = (String) object.get(CommonConstraints.PURPOSE);
        String txnId = (String) object.get(CommonConstraints.TXN_ID);

        Wallet senderWallet = walletRepository.findByContact(sender);
        Wallet receiverWallet = walletRepository.findByContact(receiver);
        String message = "txn is initiated";
        String status = "pending";

        if(senderWallet == null){
            message = "sender wallet is not associated with us";
            status = "failed";
        }else if(receiverWallet == null){
            message = "receiver wallet is not associated with us";
            status = "failed";
        }else if(amount > senderWallet.getBalance()){
            message = "sender wallet balance is lower than required";
            status = "failed";
        }else{
            walletRepository.updateWallet(sender, -amount);
            walletRepository.updateWallet(receiver, amount);
            message = "txn is suceess";
            status = "success";
        }

        JSONObject resp = new JSONObject();
        resp.put(CommonConstraints.MESSAGE, message);
        resp.put(CommonConstraints.STATUS, status);
        resp.put(CommonConstraints.TXN_ID, txnId);
        kafkaTemplate.send(CommonConstraints.TXN_UPDATED_TOPIC, resp);
    }
}
