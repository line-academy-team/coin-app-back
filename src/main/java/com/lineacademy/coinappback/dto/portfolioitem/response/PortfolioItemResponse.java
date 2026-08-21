package com.lineacademy.coinappback.dto.portfolioitem.response;

import com.lineacademy.coinappback.domain.entity.PortfolioItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PortfolioItemResponse {
    private Long id;
    private String market;
    private BigDecimal targetRatio;
    private BigDecimal buyPrice;
    private BigDecimal quantity;

    public static PortfolioItemResponse from(PortfolioItem item) {
        return PortfolioItemResponse.builder()
                .id(item.getId())
                .market(item.getMarket())
                .targetRatio(item.getTargetRatio())
                .buyPrice(item.getBuyPrice())
                .quantity(item.getQuantity())
                .build();
    }
}
