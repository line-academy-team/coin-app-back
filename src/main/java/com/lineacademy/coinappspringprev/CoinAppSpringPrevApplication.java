package com.lineacademy.coinappspringprev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class CoinAppSpringPrevApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoinAppSpringPrevApplication.class, args);
    }

}