package com.jpmc.midascore.foundation;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionSerializer implements Serializer<Transaction> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String arg0, Transaction arg1) {
        try {
            return objectMapper.writeValueAsBytes(arg1);  // Convert Transaction to JSON byte array
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Transaction", e);
        }
    }

}
