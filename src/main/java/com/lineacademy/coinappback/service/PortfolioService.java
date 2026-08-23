package com.lineacademy.coinappback.service;

import com.lineacademy.coinappback.domain.entity.Portfolio;
import com.lineacademy.coinappback.domain.entity.PortfolioItem;
import com.lineacademy.coinappback.domain.entity.User;
import com.lineacademy.coinappback.dto.portfolio.request.CreatePortfolioItemRequest;
import com.lineacademy.coinappback.dto.portfolio.request.CreatePortfolioRequest;
import com.lineacademy.coinappback.dto.portfolio.request.UpdatePortfolioRequest;
import com.lineacademy.coinappback.dto.portfolioitem.request.UpdatePortfolioItemRequest;
import com.lineacademy.coinappback.repository.PortfolioRepository;
import com.lineacademy.coinappback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true)
    public List<Portfolio> getMyPortfolios(Long userId) {
        return portfolioRepository.findAllByUserIdWithItems(userId);
    }

    @Transactional(readOnly = true)
    public Portfolio getPortfolio(Long userId, Long portfolioId) {
        return portfolioRepository.findByIdAndUserIdWithItems(portfolioId, userId)
                .orElseThrow(() -> new RuntimeException("PORTFOLIO_NOT_FOUND_OR_UNAUTHORIZED"));
    }

    @Transactional
    public Portfolio createPortfolio(Long userId, CreatePortfolioRequest request) {
        validateCreateItems(request.getItems());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .title(request.getTitle())
                .totalSeedMoney(request.getTotalSeedMoney())
                .build();

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (CreatePortfolioItemRequest itemRequest : request.getItems()) {
                PortfolioItem item = PortfolioItem.builder()
                        .portfolio(portfolio)
                        .market(itemRequest.getMarket())
                        .targetRatio(itemRequest.getTargetRatio())
                        .buyPrice(itemRequest.getBuyPrice())
                        .quantity(itemRequest.getQuantity())
                        .build();

                portfolio.addPortfolioItem(item);
            }
        }

        return portfolioRepository.save(portfolio);
    }

    @Transactional
    public Portfolio updatePortfolio(Long userId, Long portfolioId, UpdatePortfolioRequest request) {
        validateUpdateItems(request.getItems());

        Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new RuntimeException("PORTFOLIO_NOT_FOUND_OR_UNAUTHORIZED"));

        portfolio.updatePortfolioData(request.getTitle(), request.getTotalSeedMoney());

        List<PortfolioItem> existingItems = portfolio.getPortfolioItems();

        Map<String, PortfolioItem> existingItemMap = existingItems.stream()
                .collect(Collectors.toMap(PortfolioItem::getMarket, item -> item));

        List<UpdatePortfolioItemRequest> requestItems = request.getItems();
        Set<String> requestMarkets = requestItems.stream()
                .map(UpdatePortfolioItemRequest::getMarket)
                .collect(Collectors.toSet());

        existingItems.removeIf(item -> {
            return !requestMarkets.contains(item.getMarket());
        });

        for (UpdatePortfolioItemRequest itemRequest : requestItems) {
            PortfolioItem existingItem = existingItemMap.get(itemRequest.getMarket());

            if (existingItem != null) {
                existingItem.updateItemData(
                        itemRequest.getTargetRatio(),
                        itemRequest.getBuyPrice(),
                        itemRequest.getQuantity()
                );
            } else {
                PortfolioItem newItem = PortfolioItem.builder()
                        .portfolio(portfolio)
                        .market(itemRequest.getMarket())
                        .targetRatio(itemRequest.getTargetRatio())
                        .buyPrice(itemRequest.getBuyPrice())
                        .quantity(itemRequest.getQuantity())
                        .build();
                portfolio.addPortfolioItem(newItem);
            }
        }

        return portfolio;
    }

    @Transactional
    public void deletePortfolio(Long userId, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new RuntimeException("PORTFOLIO_NOT_FOUND_OR_UNAUTHORIZED"));
        portfolioRepository.delete(portfolio);
    }

    private void validateCreateItems(List<CreatePortfolioItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("INVALID_PORTFOLIO_ITEMS");
        }

        BigDecimal totalRatio = items.stream()
                .map(CreatePortfolioItemRequest::getTargetRatio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long uniqueMarkets = items.stream()
                .map(CreatePortfolioItemRequest::getMarket)
                .distinct()
                .count();

        if (totalRatio.compareTo(BigDecimal.valueOf(100)) != 0 || uniqueMarkets != items.size()) {
            throw new RuntimeException("INVALID_PORTFOLIO_ITEMS");
        }
    }

    private void validateUpdateItems(List<UpdatePortfolioItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("INVALID_PORTFOLIO_ITEMS");
        }

        BigDecimal totalRatio = items.stream()
                .map(UpdatePortfolioItemRequest::getTargetRatio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long uniqueMarkets = items.stream()
                .map(UpdatePortfolioItemRequest::getMarket)
                .distinct()
                .count();

        if (totalRatio.compareTo(BigDecimal.valueOf(100)) != 0 || uniqueMarkets != items.size()) {
            throw new RuntimeException("INVALID_PORTFOLIO_ITEMS");
        }
    }
}
