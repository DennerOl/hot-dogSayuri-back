package com.erp.pdv.dto.nfce.response;

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
public class AutorizacaoDTO {

  @JsonProperty("digest_value")
  private String digestValue;
  private String id;
  private String ambiente;
  private String status;

  @JsonProperty("autor")
  private DestinatarioDTO autor;

  @JsonProperty("codigo_status")
  private Integer codigo_status;

  @JsonProperty("motivo_status")
  private String motivo_status;

  @JsonProperty("chave_acesso")
  private String chaveAcesso;

  @JsonProperty("numero_protocolo")
  private String numeroProtocolo;

  @JsonProperty("codigo_mensagem")
  private Integer codigoMensagem;
  private String mensagem;

  @JsonProperty("tipo_evento")
  private String tipoEvento;
}
