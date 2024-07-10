package org.myworkspace.TransactionServiceApplication.Service;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TxnService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(TxnService.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TxnRepository txnRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth("txn-service", "txn-service");
        HttpEntity reqEntity = new HttpEntity(httpHeaders);
        JSONObject object =
        restTemplate.exchange("http:localhost:8080/user/userDetails?contact="+username,
                                HttpMethod.GET, reqEntity, JSONObject.class).getBody();
        List<LinkedHashMap<String, String>> list = (List<LinkedHashMap<String, String>>) (object.get("authorities"));
        List<GrantedAuthority> reqAuthorities = list.stream().map(x -> x.get("authority")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        User user = new User((String) object.get("username"), (String) object.get("password"), reqAuthorities);
        return null;
    }

    public String initTxn(String receiver, String purpose, String amount, String username) throws JsonProcessingException {
        Transaction txn = Transaction.builder().txnId(UUID.randomUUID().toString()).txnStatus(TxnStatus.INITIATED)
                .amount(Double.valueOf(amount)).Receiver(receiver).Sender(username).purpose(purpose).build();
        txnRepository.save(txn);
        JSONObject object = new JSONObject();
        object.put(CommonConstraints.AMOUNT, txn.getAmount());
        object.put(CommonConstraints.SENDER, txn.getSender());
        object.put(CommonConstraints.RECEIVER, txn.getReceiver());
        object.put(CommonConstraints.TXN_ID, txn.getTxnId());
        object.put(CommonConstraints.STATUS, txn.getTxnStatus());
        object.put(CommonConstraints.PURPOSE, txn.getPurpose());
        kafkaTemplate.send(CommonConstraints.TXN_INITIATED_TOPIC, objectMapper.writeValueAsString(object));
        logger.info("new transaction created");
        return txn.getTxnId();
    }
}
