package com.jpmc.midascore.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.component.DatabaseConduit;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRecordRepository;

    @Autowired
    private DatabaseConduit databaseConduit;

    @Transactional
    public void processTransaction(Transaction transaction) {
        UserRecord sender = databaseConduit.validateUser(transaction.getSenderId());
        UserRecord recipient = databaseConduit.validateUser(transaction.getRecipientId());

        // Validate transaction
        if (isValidTransaction(sender, recipient, transaction.getAmount())) {
            // Record transaction
            TransactionRecord record = new TransactionRecord(
                sender,
                recipient,
                transaction.getAmount()
            );

            // Save transaction record and update balances
            transactionRecordRepository.save(record);

            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + transaction.getIncentive());

            userRepository.save(sender);
            userRepository.save(recipient);

            logger.info("Transaction processed successfully. Sender: {}, Recipient: {}, Amount: {}, Incentive: {}",
                        sender.getId(), recipient.getId(), transaction.getAmount(), transaction.getIncentive());
        } else {
            logger.warn("Transaction invalid: Sender: {}, Recipient: {}, Amount: {}", 
                        transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());
        }
    }

    private boolean isValidTransaction(UserRecord sender, UserRecord recipient, double amount) {
        return sender != null && recipient != null &&
               sender.getBalance() >= amount;
    }
}
