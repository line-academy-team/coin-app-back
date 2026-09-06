package com.lineacademy.coinappback.dto.coin.response;

public record CoinDetailResponse(
        String market,
        String symbol,
        String koreanName,
        String englishName,
        double price,
        double changePrice,
        double changeRate,
        double openingPrice,
        double highPrice,
        double lowPrice,
        double tradePrice24h,
        double tradeVolume24h,
        long timestamp
) {
}
