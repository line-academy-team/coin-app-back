package com.lineacademy.coinappspringprev.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "portfolio_items",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"portfolio_id", "market"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false, length = 20)
    private String market;

    @Column(name = "target_ratio", nullable = false, precision = 5, scale = 2)
    private BigDecimal targetRatio;

    @Column(name = "buy_price", nullable = false, precision = 18, scale = 8)
    private BigDecimal buyPrice;

    @Column(nullable = false, precision = 24, scale = 8)
    private BigDecimal quantity;

    @Builder
    private PortfolioItem(Portfolio portfolio, String market, BigDecimal targetRatio, BigDecimal buyPrice, BigDecimal quantity) {
        this.portfolio = portfolio;
        this.market = market;
        this.targetRatio = targetRatio;
        this.buyPrice = buyPrice;
        this.quantity = quantity;
    }

    public void updateTargetRatio(BigDecimal targetRatio) {
        this.targetRatio = targetRatio;
    }

    public void updateQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}