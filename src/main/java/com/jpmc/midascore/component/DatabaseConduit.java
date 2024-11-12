package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.*;

import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;

    public DatabaseConduit(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public UserRecord validateUser(long id) {
        return userRepository.findById(id);
    }

    public float validateAmount(long sId, float amount) {
        UserRecord sender = validateUser(sId);
        float balance = sender.getBalance();
        if (amount <= balance) {
            return amount;
        }

        return 0f;
    }

}
