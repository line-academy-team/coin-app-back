package com.lineacademy.coinappback.dto.coin.response;

public record CoinResponse(
        String market,
        String symbol,
        String koreanName,
        String englishName,
        double price,
        double changeRate
) {
}
