package com.lineacademy.coinappback.controller;

import com.lineacademy.coinappback.domain.entity.Portfolio;
import com.lineacademy.coinappback.dto.portfolio.request.UpdatePortfolioRequest;
import com.lineacademy.coinappback.dto.portfolio.request.CreatePortfolioRequest;
import com.lineacademy.coinappback.dto.portfolio.response.PortfolioResponse;
import com.lineacademy.coinappback.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyPortfolios(
            @AuthenticationPrincipal Long userId
    ) {
        List<Portfolio> portfolios = portfolioService.getMyPortfolios(userId);

        List<PortfolioResponse> responseList = portfolios.stream()
                .map(PortfolioResponse::from)
                .toList();

        return ResponseEntity.ok(Map.of(
                "message", "포트폴리오 목록을 성공적으로 불러왔습니다.",
                "data", responseList
        ));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePortfolio(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdatePortfolioRequest request
    ) {
        try {
            Portfolio portfolio = portfolioService.updatePortfolio(userId, id, request);

            return ResponseEntity.ok(Map.of(
                    "message", "포트폴리오가 성공적으로 수정되었습니다.",
                    "data", PortfolioResponse.from(portfolio)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("PORTFOLIO_NOT_FOUND_OR_UNAUTHORIZED")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "접근 권한이 없거나 존재하지 않는 포트폴리오입니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "포트폴리오 수정 중 서버 에러가 발생했습니다."
            ));
        }
    }

    public ResponseEntity<Map<String, Object>> deletePortfolio(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id
    ) {
        try {
            portfolioService.deletePortfolio(userId, id);

            return ResponseEntity.ok(Map.of(
                    "message", "포트폴리오가 성공적으로 삭제되었습니다."
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("PORTFOLIO_NOT_FOUND_OR_UNAUTHORIZED")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "접근 권한이 없거나 존재하지 않는 포트폴리오입니다."
                ));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "포트폴리오 삭제 중 서버 에러가 발생했습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPortfolio(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreatePortfolioRequest request
    ) {
        try {
            Portfolio portfolio = portfolioService.createPortfolio(userId, request);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "포트폴리오가 성공적으로 생성되었습니다.",
                    "data", PortfolioResponse.from(portfolio)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "해당 사용자를 찾을 수 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "포트폴리오 생성 중 서버 에러가 발생했습니다."
            ));
        }
    }
}
