package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KafkaProducer {
    private final String topic;

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DatabaseConduit databaseConduit;

    public KafkaProducer(@Value("${general.kafka-topic}") String topic, KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String transactionLine) {
        String[] transactionData = transactionLine.split(", ");

        Long senderId = Long.parseLong(transactionData[0]);
        Long recipientId = Long.parseLong(transactionData[1]);
        Float amount = Float.parseFloat(transactionData[2]);

        Transaction transaction = new Transaction(
            databaseConduit.validateUser(senderId).getId(),
            databaseConduit.validateUser(recipientId).getId(),
            databaseConduit.validateAmount(senderId, amount)
        );

        getIncentiveAmount(transaction);

        kafkaTemplate.send(topic, transaction);
    }

    private void getIncentiveAmount(Transaction transaction) {
        ResponseEntity<Incentive> response = restTemplate.postForEntity(
            "http://localhost:8080/incentive",
            transaction,
            Incentive.class
        );

        float incentiveAmount = response.getBody().getAmount();
        transaction.setIncentive(incentiveAmount);
    }
}