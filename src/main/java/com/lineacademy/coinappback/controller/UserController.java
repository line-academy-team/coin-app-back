package com.lineacademy.coinappback.controller;

import com.lineacademy.coinappback.domain.entity.User;
import com.lineacademy.coinappback.dto.user.request.*;
import com.lineacademy.coinappback.dto.user.response.UserResponse;
import com.lineacademy.coinappback.service.UserService;
import com.lineacademy.coinappback.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "성공적으로 회원가입 되었습니다.",
                            "data", UserResponse.from(user)
                    ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ALREADY_EXISTS_EMAIL"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "message", "이미 가입된 이메일입니다."
                ));
            if (e.getMessage().equals("ALREADY_EXISTS_NICKNAME"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "message", "이미 사용 중인 닉네임입니다."
                ));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "서버 에러가 발생되었습니다."
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            String token = jwtUtil.generateToken(user.getId());

            return ResponseEntity.ok(Map.of(
                    "message", "로그인에 성공했습니다",
                    "data", Map.of(
                            "user", UserResponse.from(user),
                            "token", token
                    )
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INVALID_CREDENTIALS")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "message", "아이디 또는 비밀번호가 일치하지 않습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "서버 에러"
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(
            @AuthenticationPrincipal Long userId
    ) {
        try {
            User user = userService.getMe(userId);

            return ResponseEntity.ok(Map.of(
                    "message", "사용자 정보를 성공적으로 불러왔습니다.",
                    "data", UserResponse.from(user)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "해당 사용자를 찾을 수 없습니다."
                ));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "서버 에러가 발생되었습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        try {
            User updatedUser = userService.updateUser(currentUserId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "회원정보가 성공적으로 수정되었습니다.",
                    "data", UserResponse.from(updatedUser)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "해당 사용자를 찾을 수 없습니다."
                ));
            if (e.getMessage().equals("DUPLICATED_NICKNAME"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "message", "이미 사용 중인 닉네임입니다."
                ));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "서버 에러가 발생되었습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        try {
            userService.updatePassword(currentUserId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "비밀번호가 성공적으로 변경되었습니다."
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            if (e.getMessage().equals("INVALID_CREDENTIALS"))
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 에러가 발생되었습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdrawUser(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody WithdrawUserRequest request
    ) {
        try {
            userService.withdrawUser(userId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "회원정보가 성공적으로 탈퇴되었습니다."
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "해당 사용자를 찾을 수 없습니다."
                ));
            if (e.getMessage().equals("INVALID_CREDENTIALS"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "message", "입력하신 비밀번호가 일치하지 않습니다."
                ));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "서버 에러가 발생되었습니다."
            ));
        }
    }
}