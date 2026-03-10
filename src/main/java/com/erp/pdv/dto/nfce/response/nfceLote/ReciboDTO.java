package com.erp.pdv.dto.nfce.response.nfceLote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReciboDTO {

  private String numero;

  @JsonProperty("codigo_status")
  private Integer codigoStatus;

  @JsonProperty("motivo_status")
  private String motivoStatus;

  @JsonProperty("codigo_mensagem")
  private Integer codigoMensagem;

  private String mensagem;

}
