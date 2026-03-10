package com.erp.pdv.dto.autenticationFiscal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2TokenResponse {

  private String access_token;
  private String token_type;
  private int expires_in;
  private String scope;

}
