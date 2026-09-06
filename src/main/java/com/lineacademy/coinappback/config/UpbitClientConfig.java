package com.lineacademy.coinappback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class UpbitClientConfig {

    @Bean
    public RestClient upbitRestClient(RestClient.Builder builder) {
        return builder.baseUrl("https://api.upbit.com/v1").build();
    }

    @Bean
    public RestClient upbitStaticRestClient(RestClient.Builder builder) {
        return builder.baseUrl("https://static.upbit.com/logos").build();
    }
}
