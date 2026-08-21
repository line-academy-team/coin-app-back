package com.lineacademy.coinappback.repository;

import com.lineacademy.coinappback.domain.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Optional<Portfolio> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT DISTINCT p FROM Portfolio p LEFT JOIN FETCH p.portfolioItems WHERE p.user.id = :userId")
    List<Portfolio> findAllByUserIdWithItems(@Param("userId") Long userId);
}
