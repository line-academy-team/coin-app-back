package com.lineacademy.coinappback.dto.portfolio.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class CreatePortfolioRequest {

    @NotBlank(message = "포트폴리오 제목을 입력해주세요.")
    private String title;

    @NotNull(message = "총 시드머니를 입력해주세요.")
    private BigDecimal totalSeedMoney;

    @Valid
    private List<CreatePortfolioItemRequest> items;
}
