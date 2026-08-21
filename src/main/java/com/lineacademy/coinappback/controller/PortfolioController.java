package com.lineacademy.coinappback.controller;

import com.lineacademy.coinappback.domain.entity.Portfolio;
import com.lineacademy.coinappback.dto.portfolio.response.PortfolioResponse;
import com.lineacademy.coinappback.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
