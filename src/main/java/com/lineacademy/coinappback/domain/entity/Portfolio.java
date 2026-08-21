package com.lineacademy.coinappback.domain.entity;

import com.lineacademy.coinappback.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "total_seed_money", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalSeedMoney;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<PortfolioItem> portfolioItems = new ArrayList<>();

    @Builder
    private Portfolio(User user, String title, BigDecimal totalSeedMoney) {
        this.user = user;
        this.title = title;
        this.totalSeedMoney = totalSeedMoney;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
