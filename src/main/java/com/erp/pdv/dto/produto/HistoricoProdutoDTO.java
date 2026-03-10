package com.erp.pdv.dto.produto;

import java.time.Instant;

import com.erp.pdv.utils.TipoMovimento;

import lombok.Data;

@Data
public class HistoricoProdutoDTO {

  private Long id;
  private Long productId;
  private TipoMovimento tipoMovimento;
  private Double quantidade;
  private Double saldoAtual;
  private Double saldoAnterior;

  private String referencia;
  private Instant criadoEm;

}
