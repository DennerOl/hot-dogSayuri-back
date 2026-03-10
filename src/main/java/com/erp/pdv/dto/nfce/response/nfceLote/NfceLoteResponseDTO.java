package com.erp.pdv.dto.nfce.response.nfceLote;

import java.util.List;

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
public class NfceLoteResponseDTO {

  private String id;
  private String status; // registrado
  private String ambiente;
  private String referencia;

  @JsonProperty("id_lote")
  private String idLote;

  private ReciboDTO recibo;
  private List<DocumentoDTO> documentos;
}
