package com.lineacademy.coinappback.dto.portfolio.response;

import com.lineacademy.coinappback.domain.entity.Portfolio;
import com.lineacademy.coinappback.dto.portfolioitem.response.PortfolioItemResponse;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PortfolioResponse {
    private Long id;
    private String title;
    private BigDecimal totalAmount;
    private BigDecimal returnRate;
    private String tags;
    private String icon;
    private List<PortfolioItemResponse> items;

    public static PortfolioResponse from(Portfolio portfolio) {
        List<PortfolioItemResponse> itemResponses = portfolio.getPortfolioItems().stream()
                .map(PortfolioItemResponse::from)
                .collect(Collectors.toList());

        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .title(portfolio.getTitle())
                .totalAmount(portfolio.getTotalSeedMoney())
                .returnRate(BigDecimal.ZERO)
                .tags("포트폴리오 구성 전")
                .icon("shield-checkmark")
                .items(itemResponses)
                .build();
    }
}