package com.erp.pdv.model.produto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.erp.pdv.model.nfce.ItemNfce;
import com.erp.pdv.model.nfce.Nfce;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "descricao")
  private String x_prod; // Descrição do produto (xProd na API)

  @Column(name = "codigo_principal")
  private String c_prod; // Código do produto

  @Column(name = "codigo_barras")
  private String c_EAN; // GTIN da unidade tributável

  @Column(name = "ncm")
  private String NCM; // Unidade comercial

  @Column(name = "cest")
  private String CEST;

  @Column(name = "cfop")
  private String CFOP; // Código Fiscal de Operações e Prestações

  @Column(name = "u_com")
  private String u_com; // Unidade comercial ex UN Ou KG

  @Column(name = "preco_venda")
  private Double v_un_com; // Valor unitário para venda

  @Column(name = "q_trib")
  private Double q_trib; // Quantidade tributável = qCom

  @Column(name = "vl_un_trib")
  private Double v_un_trib; // Valor unitário tributável = vUnCom

  @Column(name = "ind_tot")
  private Double ind_tot; // Indicador de totalização 1 para nomal e 0 para desconto

  @Column(name = "preco_custo")
  private Double preco_custo;

  @Column(name = "quantidade")
  private Double quantidade; // Quantidade do produto

  @Column(name = "valor_total_est")
  private Double v_prod; // Valor total do item (qCom * vUnCom)

  private Boolean ativo;

  @Column(columnDefinition = "TEXT")
  private String imagemUrl;

  @OneToMany(mappedBy = "id.product")
  private Set<ItemNfce> itens = new HashSet<>();

  private String cst;

  private Double icmsAliquota;

  private Double fcpAliquota;

  private Integer origem;

  private Integer modalidadeBc;

  private String uf;

  @Column(name = "tributo_fiscal_id")
  private Long tributoFiscalId;

  public List<Nfce> getNfces() {
    return itens.stream().map(x -> x.getNfce()).toList();
  }

  public Set<ItemNfce> getItens() {
    return itens;
  }

  public void calcularValorTotal() {
    if (this.quantidade != null && this.preco_custo != null) {
      this.v_prod = Math.round((this.quantidade * this.preco_custo) * 100.0) / 100.0;
    } else {
      this.v_prod = 0.0;
    }
  }

}

// private Double qCom; // Quantidade para venda

// @Column(name = "tipo_2", length = 3)
// private String uTrib; // Unidade tributável UN ou KG = uCom
