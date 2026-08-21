package com.lineacademy.coinappback.service;

import com.lineacademy.coinappback.domain.entity.User;
import com.lineacademy.coinappback.dto.user.request.*;
import com.lineacademy.coinappback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("ALREADY_EXISTS_EMAIL");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("ALREADY_EXISTS_NICKNAME");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("INVALID_CREDENTIALS"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        return user;
    }

    @Transactional
    public User updateUser(Long currentUserId, UpdateUserRequest request) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

    if (user.getDeletedAt() != null) {
        throw new RuntimeException("USER_NOT_FOUND")
    }
        if (user.getDeletedAt() != null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        if (request.getNickname() != null) {
            if (userRepository.existsByNicknameAndIdNot(request.getNickname(), currentUserId)) {
                throw new RuntimeException("DUPLICATED_NICKNAME");
            }
            user.updateNickname(request.getNickname());
        }

        return user;
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void withdrawUser(Long userId, WithdrawUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        user.markAsDeleted();
    }
}
