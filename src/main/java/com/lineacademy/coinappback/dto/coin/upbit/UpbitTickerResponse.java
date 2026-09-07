package com.lineacademy.coinappback.dto.coin.upbit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpbitTickerResponse(
        String market,
        @JsonProperty("opening_price") double openingPrice,
        @JsonProperty("high_price") double highPrice,
        @JsonProperty("low_price") double lowPrice,
        @JsonProperty("trade_price") double tradePrice,
        @JsonProperty("signed_change_price") double signedChangePrice,
        @JsonProperty("signed_change_rate") double signedChangeRate,
        @JsonProperty("acc_trade_price_24h") double accumulatedTradePrice24h,
        @JsonProperty("acc_trade_volume_24h") double accumulatedTradeVolume24h,
        long timestamp
) {
}
