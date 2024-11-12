package com.jpmc.midascore.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.services.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TransactionListener {

    @Autowired
    private TransactionService transactionService;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void listen(Transaction transaction) {
        transactionService.processTransaction(transaction);
    }
}
