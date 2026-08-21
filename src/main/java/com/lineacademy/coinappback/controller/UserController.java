package com.lineacademy.coinappback.controller;

import com.lineacademy.coinappback.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

public class UserController {


    @PreAuthorize("isAuthenticated")
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
}
