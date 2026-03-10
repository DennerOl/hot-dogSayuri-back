package com.erp.pdv.dto.nfce.response.nfceLote;

import java.time.LocalDateTime;

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
public class DocumentoDTO {

  private String id;
  private String ambiente;

  private String status;
  private String referencia;

  @JsonProperty("data_emissao")
  private LocalDateTime dataEmissao;

  private String chave;
  private AutorizacaoLoteDTO autorizacao;

}
