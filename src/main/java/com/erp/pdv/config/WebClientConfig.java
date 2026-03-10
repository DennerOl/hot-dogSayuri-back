package com.erp.pdv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient.Builder webClientBuilder() {
    final int bufferSize = 5 * 1024 * 1024; // 1MB
    final ExchangeStrategies strategies = ExchangeStrategies
        .builder()
        .codecs(configurer -> configurer.defaultCodecs()
            .maxInMemorySize(bufferSize))
        .build();
    return WebClient.builder()
        .exchangeStrategies(strategies);
  }

}
