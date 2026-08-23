package com.lineacademy.coinappback.dto.portfolioitem.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdatePortfolioItemRequest {
    @NotBlank(message = "마켓 정보는 필수입니다.")
    private String market;

    @NotNull(message = "목표 비중은 필수입니다.")
    @DecimalMin(value = "0.01", message = "목표 비중은 0보다 커야 합니다.")
    @DecimalMax(value = "100", message = "목표 비중은 100 이하여야 합니다.")
    private BigDecimal targetRatio;

    @NotNull(message = "매수 가격은 필수입니다.")
    @DecimalMin(value = "0.00000001", message = "매수 가격은 0보다 커야 합니다.")
    private BigDecimal buyPrice;

    @NotNull(message = "수량은 필수입니다.")
    @DecimalMin(value = "0.00000001", message = "수량은 0보다 커야 합니다.")
    private BigDecimal quantity;
}
