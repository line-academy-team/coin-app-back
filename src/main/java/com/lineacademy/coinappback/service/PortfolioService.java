package com.lineacademy.coinappback.service;

import com.lineacademy.coinappback.domain.entity.Portfolio;
import com.lineacademy.coinappback.domain.entity.PortfolioItem;
import com.lineacademy.coinappback.dto.portfolio.request.UpdatePortfolioRequest;
import com.lineacademy.coinappback.dto.portfolioitem.request.UpdatePortfolioItemRequest;
import com.lineacademy.coinappback.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true)
    public List<Portfolio> getMyPortfolios(Long userId) {
        return portfolioRepository.findAllByUserIdWithItems(userId);
    }

    @Transactional
    public Portfolio updatePortfolio(Long userId, Long portfolioId, UpdatePortfolioRequest request) {
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
            boolean isRemoved = !requestMarkets.contains(item.getMarket());
            if (isRemoved) {
                item.assignPortfolio(null);
            }
            return isRemoved;
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
}
