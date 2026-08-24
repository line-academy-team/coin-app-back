package com.lineacademy.coinappback.dto.portfolio.request;

import com.lineacademy.coinappback.dto.portfolioitem.request.UpdatePortfolioItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class UpdatePortfolioRequest {
    @NotBlank(message = "포트폴리오 제목을 입력해주세요.")
    private String title;

    @NotNull(message = "총 시드머니를 입력해주세요.")
    @DecimalMin(value = "100000", message = "총 시드머니는 10만원 이상이어야 합니다.")
    private BigDecimal totalSeedMoney;

    @NotEmpty(message = "한 개 이상의 코인을 선택해주세요.")
    private List<@Valid UpdatePortfolioItemRequest> items;
}
