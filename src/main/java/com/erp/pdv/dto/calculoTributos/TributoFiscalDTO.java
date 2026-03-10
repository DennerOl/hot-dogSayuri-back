package com.erp.pdv.dto.calculoTributos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TributoFiscalDTO {

  private Long id;
  private String descricao;
  private String cst;
  @JsonProperty("icms_aliquota")
  private Double icmsAliquota;
  @JsonProperty("fcp_aliquota")
  private Double fcpAliquota;
  private Integer origem;
  private Integer modalidadeBc;
  private String uf;
  private Boolean ativo;

}
