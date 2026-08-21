package com.lineacademy.coinappback.service;

import com.lineacademy.coinappback.domain.entity.User;
import org.springframework.transaction.annotation.Transactional;

public class UserService {


    @Transactional(readOnly = true)
    public User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

    if (user.getDeletedAt() != null) {
        throw new RuntimeException("USER_NOT_FOUND")
    }

    return user;
}
