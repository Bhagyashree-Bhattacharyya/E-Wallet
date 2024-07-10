package org.myworkspace.TransactionServiceApplication.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.myworkspace.TransactionServiceApplication.Entities.Transaction;
import org.myworkspace.TransactionServiceApplication.Entities.TxnStatus;
import org.myworkspace.TransactionServiceApplication.Repository.TxnRepository;
import org.myworkspace.Utilities.CommonConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TxnUpdateConsumer {

    private static Logger logger = LoggerFactory.getLogger(TxnUpdateConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TxnRepository txnRepository;

    @KafkaListener(topics = CommonConstraints.TXN_INITIATED_TOPIC, groupId = "txn-group")
    public void updateTxn(String msg) throws JsonProcessingException {
        JSONObject object = objectMapper.readValue(msg, JSONObject.class);
        String status = (String) object.get(CommonConstraints.STATUS);
        String txnId = (String) object.get(CommonConstraints.TXN_ID);
        String message = (String) object.get(CommonConstraints.MESSAGE);

        Transaction txn = txnRepository.findByTxnId(txnId);
        if (txn != null) {
            txn.setTxnStatus(TxnStatus.valueOf(status));
            txnRepository.save(txn);
        } else {
            logger.error("Transaction not found with ID: {}", txnId);
        }

//        String userContact = txn.getSender();  --- need to check on how to send notification
//        notificationService.sendNotification(userContact, message);

        logger.info("Transaction update processed: {}", msg);
    }
}
