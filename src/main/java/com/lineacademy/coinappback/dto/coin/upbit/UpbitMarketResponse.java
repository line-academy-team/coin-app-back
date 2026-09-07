package com.lineacademy.coinappback.dto.coin.upbit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpbitMarketResponse(
        String market,
        @JsonProperty("korean_name") String koreanName,
        @JsonProperty("english_name") String englishName
) {
}
