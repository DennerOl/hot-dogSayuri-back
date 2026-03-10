package com.erp.pdv.controllers.autenticationFiscal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.pdv.dto.autenticationFiscal.OAuth2TokenResponse;
import com.erp.pdv.service.autenticationFiscal.AuthTokenService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/fiscal")
public class AuthTokenController {

  @Autowired
  private AuthTokenService authTokenService;

  @GetMapping(value = "/auth")
  public Mono<ResponseEntity<OAuth2TokenResponse>> tokenFiscal() {

    return authTokenService.authenticate()
        .map(response -> ResponseEntity.ok(response))
        .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).<OAuth2TokenResponse>build()));

  }

}
