package com.lineacademy.coinappback.service;

import com.lineacademy.coinappback.dto.coin.response.CoinDetailResponse;
import com.lineacademy.coinappback.dto.coin.response.CoinResponse;
import com.lineacademy.coinappback.dto.coin.response.CoinTickerResponse;
import com.lineacademy.coinappback.dto.coin.upbit.UpbitMarketResponse;
import com.lineacademy.coinappback.dto.coin.upbit.UpbitTickerResponse;
import com.lineacademy.coinappback.exception.UpbitApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UpbitService {
    private static final Pattern KRW_MARKET_PATTERN = Pattern.compile("KRW-[A-Z0-9]+$");
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("[A-Z0-9]{1,20}$");

    private final RestClient upbitRestClient;
    private final RestClient upbitStaticRestClient;

    public UpbitService(
            @Qualifier("upbitRestClient") RestClient upbitRestClient,
            @Qualifier("upbitStaticRestClient") RestClient upbitStaticRestClient
    ) {
        this.upbitRestClient = upbitRestClient;
        this.upbitStaticRestClient = upbitStaticRestClient;
    }

    public List<CoinResponse> getKrwCoins() {
        List<UpbitMarketResponse> markets = getMarkets();
        Map<String, UpbitMarketResponse> marketsByCode = markets.stream()
                .collect(Collectors.toMap(UpbitMarketResponse::market, market -> market));

        return getAllKrwTickers().stream()
                .filter(ticker -> ticker.market().startsWith("KRW-"))
                .map(ticker -> {
                    UpbitMarketResponse market = marketsByCode.get(ticker.market());
                    return new CoinResponse(
                            ticker.market(),
                            ticker.market().substring("KRW-".length()),
                            market == null ? "" : market.koreanName(),
                            market == null ? "" : market.englishName(),
                            ticker.tradePrice(),
                            ticker.signedChangeRate() * 100
                    );
                })
                .toList();
    }

    public CoinDetailResponse getCoin(String market) {
        UpbitTickerResponse ticker = getTicker(validateMarket(market));
        UpbitMarketResponse marketInfo = getMarkets().stream()
                .filter(item -> item.market().equals(ticker.market()))
                .findFirst()
                .orElse(null);

        return new CoinDetailResponse(
                ticker.market(),
                ticker.market().substring("KRW-".length()),
                marketInfo == null ? "" : marketInfo.koreanName(),
                marketInfo == null ? "" : marketInfo.englishName(),
                ticker.tradePrice(),
                ticker.signedChangePrice(),
                ticker.signedChangeRate() * 100,
                ticker.openingPrice(),
                ticker.highPrice(),
                ticker.lowPrice(),
                ticker.accumulatedTradePrice24h(),
                ticker.accumulatedTradeVolume24h(),
                ticker.timestamp()
        );
    }

    public CoinTickerResponse getCoinTicker(String market) {
        UpbitTickerResponse ticker = getTicker(validateMarket(market));
        return new CoinTickerResponse(
                ticker.tradePrice(),
                ticker.signedChangePrice(),
                ticker.signedChangeRate() * 100,
                ticker.openingPrice(),
                ticker.highPrice(),
                ticker.lowPrice(),
                ticker.accumulatedTradePrice24h(),
                ticker.accumulatedTradeVolume24h(),
                ticker.timestamp()
        );
    }

    public byte[] getCoinIcon(String symbol) {
        String validatedSymbol = validateSymbol(symbol);
        try {
            byte[] icon = upbitStaticRestClient.get()
                    .uri("/{symbol}.png", validatedSymbol)
                    .retrieve()
                    .body(byte[].class);
            if (icon == null) {
                throw new UpbitApiException("업비트 아이콘 응답이 비어 있습니다.");
            }
            return icon;
        } catch (RestClientException exception) {
            throw new UpbitApiException("업비트 아이콘을 불러오지 못했습니다.", exception);
        }
    }

    private List<UpbitMarketResponse> getMarkets() {
        return getList(
                upbitRestClient.get().uri("/market/all"),
                new ParameterizedTypeReference<List<UpbitMarketResponse>>() {},
                "업비트 마켓 정보를 불러오지 못했습니다."
        );
    }

    private List<UpbitTickerResponse> getAllKrwTickers() {
        return getList(
                upbitRestClient.get().uri("/ticker/all?quote_currencies=KRW"),
                new ParameterizedTypeReference<List<UpbitTickerResponse>>() {},
                "업비트 현재가 정보를 불러오지 못했습니다."
        );
    }

    private UpbitTickerResponse getTicker(String market) {
        List<UpbitTickerResponse> tickers = getList(
                upbitRestClient.get().uri(builder -> builder.path("/ticker")
                        .queryParam("markets", market)
                        .build()),
                new ParameterizedTypeReference<List<UpbitTickerResponse>>() {},
                "업비트 현재가 정보를 불러오지 못했습니다."
        );
        return tickers.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 코인 정보를 찾을 수 없습니다."));
    }

    private <T> List<T> getList(
            RestClient.RequestHeadersSpec<?> request,
            ParameterizedTypeReference<List<T>> responseType,
            String errorMessage
    ) {
        try {
            List<T> response = request.retrieve()
                    .body(responseType);
            if (response == null) {
                throw new UpbitApiException(errorMessage);
            }
            return response;
        } catch (RestClientException exception) {
            throw new UpbitApiException(errorMessage, exception);
        }
    }

    private String validateMarket(String market) {
        if (market == null || !KRW_MARKET_PATTERN.matcher(market).matches()) {
            throw new IllegalArgumentException("유효하지 않은 KRW 마켓입니다.");
        }
        return market;
    }

    private String validateSymbol(String symbol) {
        if (symbol == null || !SYMBOL_PATTERN.matcher(symbol).matches()) {
            throw new IllegalArgumentException("유효하지 않은 코인 심볼입니다.");
        }
        return symbol;
    }
}
