package com.lineacademy.coinappback.dto.portfolio.response;

import com.lineacademy.coinappback.domain.entity.Portfolio;
import com.lineacademy.coinappback.dto.portfolioitem.response.PortfolioItemResponse;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PortfolioResponse {
    private Long id;
    private LocalDateTime createdAt;
    private String title;
    private BigDecimal totalSeedMoney;
    private String tags;
    private String icon;
    private List<PortfolioItemResponse> coins;

    public static PortfolioResponse from(Portfolio portfolio) {
        List<PortfolioItemResponse> itemResponses = portfolio.getPortfolioItems().stream()
                .map(PortfolioItemResponse::from)
                .collect(Collectors.toList());

        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .createdAt(portfolio.getCreatedAt())
                .title(portfolio.getTitle())
                .totalSeedMoney(portfolio.getTotalSeedMoney())
                .tags("포트폴리오 구성 전")
                .icon("shield-checkmark")
                .coins(itemResponses)
                .build();
    }
}
