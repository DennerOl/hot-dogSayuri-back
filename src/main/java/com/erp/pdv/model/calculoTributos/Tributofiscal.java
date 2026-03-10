package com.erp.pdv.model.calculoTributos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tributo_fiscal")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Tributofiscal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String descricao;
  private String cst;
  @Column(name = "icms_aliquota")
  private Double icmsAliquota;
  @Column(name = "fcp_aliquota")
  private Double fcpAliquota;
  private Integer origem;
  private Integer modalidadeBc;
  private String uf;
  private Boolean ativo;

}
