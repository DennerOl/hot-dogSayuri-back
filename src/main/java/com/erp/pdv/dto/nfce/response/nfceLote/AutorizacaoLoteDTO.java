package com.erp.pdv.dto.nfce.response.nfceLote;

import com.erp.pdv.dto.DestinatarioDTO;
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
public class AutorizacaoLoteDTO {

  @JsonProperty("chave_acesso")
  private String chaveAcesso;

  @JsonProperty("numero_protocolo")
  private String numeroProtocolo;

  @JsonProperty("motivo_status")
  private String motivoStatus;

  @JsonProperty("codigo_mensagem")
  private Integer codigoMensagem;

  private String mensagem;

  private String status;

  private DestinatarioDTO autor;

}
