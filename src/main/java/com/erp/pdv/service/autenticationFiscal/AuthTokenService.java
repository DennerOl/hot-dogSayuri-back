package com.erp.pdv.service.autenticationFiscal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.erp.pdv.dto.autenticationFiscal.OAuth2TokenResponse;

import reactor.core.publisher.Mono;

@Service
public class AuthTokenService {

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenService.class);

  @Value("${api.url}")
  private String authApiUrl;

  @Value("${fiscal.id}")
  private String clientId;

  @Value("${fiscal.secret}")
  private String clientSecret;

  private final WebClient webClient;

  public AuthTokenService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  public Mono<OAuth2TokenResponse> authenticate() {
    logger.info("Iniciando autenticação fiscal com clientId");

    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "client_credentials");
    formData.add("client_id", clientId);
    formData.add("client_secret", clientSecret);
    formData.add("scope", "cep cnpj nfe nfce nfse cte mdfe");

    return webClient.post()
        .uri(authApiUrl + "/oauth/token")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .bodyValue(formData)
        .retrieve()
        .bodyToMono(OAuth2TokenResponse.class)

        .doOnSuccess(response -> logger.info("Token de acesso obtido com sucesso: "))

        .doOnError(error -> {
          throw new RuntimeException("Erro ao obter token de acesso: " + error.getMessage(), error);
        })

        .onErrorMap(erro -> {
          logger.error("Erro ao obter token de acesso: {}", erro.getMessage());
          return new RuntimeException("Erro ao obter token de acesso", erro);
        });
  }

}
