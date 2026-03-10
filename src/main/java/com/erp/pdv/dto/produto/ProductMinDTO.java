package com.erp.pdv.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductMinDTO {

  private Long id;

  @JsonProperty("x_prod")
  private String x_prod;

  @JsonProperty("c_prod")
  private String c_prod;

  @JsonProperty("c_EAN")
  private String c_EAN;

  @JsonProperty("NCM")
  private String NCM;

  @JsonProperty("CEST")
  private String CEST;

  @JsonProperty("CFOP")
  private String CFOP;

  @JsonProperty("u_com")
  private String u_com;

  @JsonProperty("v_un_com")
  private Double v_un_com;

  @JsonProperty("q_trib")
  private Double q_trib;

  @JsonProperty("v_un_trib")
  private Double v_un_trib;

  @JsonProperty("ind_tot")
  private Double ind_tot;

  @JsonProperty("preco_custo")
  private Double preco_custo;

  @JsonProperty("quantidade")
  private double quantidade;

  @JsonProperty("v_prod")
  private Double v_prod;

  @JsonProperty("ativo")
  private Boolean ativo;

  private String imagemUrl;

  private String cst;

  @JsonProperty("icms_aliquota")
  private Double icmsAliquota;

  @JsonProperty("fcp_aliquota")
  private Double fcpAliquota;

  private Integer origem;

  private Integer modalidadeBc;

  private String uf;

  @JsonProperty("tributo_fiscal_id")
  private Long tributoFiscalId;

}
