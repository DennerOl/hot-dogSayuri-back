package com.erp.pdv.model.produto;

import java.time.Instant;

import com.erp.pdv.utils.TipoMovimento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_historico_produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoProduto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_movimento")
  private TipoMovimento tipoMovimento; // e.g., "SALE", "RESTOCK"

  @Column(name = "quantidade")
  private Double quantidade;

  @Column(name = "saldo_atual")
  private Double saldoAtual;

  @Column(name = "saldo_anterior")
  private Double saldoAnterior;

  @Column(name = "referencia")
  private String referencia; // e.g. NFC-e ID

  @Column(name = "criado_em")
  private Instant criadoEm;
}
