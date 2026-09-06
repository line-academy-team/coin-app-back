package com.lineacademy.coinappback.controller;

import com.lineacademy.coinappback.dto.coin.response.CoinDetailResponse;
import com.lineacademy.coinappback.dto.coin.response.CoinResponse;
import com.lineacademy.coinappback.dto.coin.response.CoinTickerResponse;
import com.lineacademy.coinappback.exception.UpbitApiException;
import com.lineacademy.coinappback.service.UpbitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coins")
@RequiredArgsConstructor
public class CoinController {
    private final UpbitService upbitService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCoins() {
        try {
            List<CoinResponse> coins = upbitService.getKrwCoins();
            return ResponseEntity.ok(Map.of("message", "코인 목록을 성공적으로 불러왔습니다.", "data", coins));
        } catch (UpbitApiException exception) {
            return upbitError(exception);
        }
    }

    @GetMapping("/{market}/ticker")
    public ResponseEntity<Map<String, Object>> getTicker(@PathVariable String market) {
        try {
            CoinTickerResponse ticker = upbitService.getCoinTicker(market);
            return ResponseEntity.ok(Map.of("message", "코인 현재가를 성공적으로 불러왔습니다.", "data", ticker));
        } catch (IllegalArgumentException exception) {
            return badRequest(exception);
        } catch (UpbitApiException exception) {
            return upbitError(exception);
        }
    }

    @GetMapping("/{market}")
    public ResponseEntity<Map<String, Object>> getCoin(@PathVariable String market) {
        try {
            CoinDetailResponse coin = upbitService.getCoin(market);
            return ResponseEntity.ok(Map.of("message", "코인 정보를 성공적으로 불러왔습니다.", "data", coin));
        } catch (IllegalArgumentException exception) {
            return badRequest(exception);
        } catch (UpbitApiException exception) {
            return upbitError(exception);
        }
    }

    @GetMapping("/icons/{symbol}")
    public ResponseEntity<?> getCoinIcon(@PathVariable String symbol) {
        try {
            byte[] icon = upbitService.getCoinIcon(symbol);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                    .body(icon);
        } catch (IllegalArgumentException exception) {
            return badRequest(exception);
        } catch (UpbitApiException exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("message", exception.getMessage()));
        }
    }

    private ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }

    private ResponseEntity<Map<String, Object>> upbitError(UpbitApiException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("message", exception.getMessage()));
    }
}
