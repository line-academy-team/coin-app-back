package com.lineacademy.coinappback.repository;

import com.lineacademy.coinappback.domain.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    @Query("SELECT DISTINCT p FROM Portfolio p LEFT JOIN FETCH p.portfolioItems WHERE p.user.id = :userId")
    List<Portfolio> findAllByUserIdWithItems(@Param("userId") Long userId);
}
