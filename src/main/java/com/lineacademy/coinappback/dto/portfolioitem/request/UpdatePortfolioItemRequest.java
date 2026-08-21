package com.lineacademy.coinappback.dto.portfolioitem.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdatePortfolioItemRequest {
    @NotBlank(message = "마켓 정보는 필수입니다.")
    private String market;

    @NotNull(message = "목표 비중은 필수입니다.")
    private BigDecimal targetRatio;

    @NotNull(message = "매수 가격은 필수입니다.")
    private BigDecimal buyPrice;

    @NotNull(message = "수량은 필수입니다.")
    private BigDecimal quantity;
}
