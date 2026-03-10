package com.erp.pdv.model.nfce;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.erp.pdv.model.Destinatario;
import com.erp.pdv.model.Empresa;
import com.erp.pdv.model.Pagamento;
import com.erp.pdv.model.produto.Product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_nfce")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Nfce {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // Identificador único da NFC-e no banco

  @Column(name = "id_api")
  private String idNfce;

  private String ambiente;

  private String mensagem;

  private Integer codigoMensagem;

  @Column(name = "codigo_uf")
  private Integer cUF = 33; // Código da UF do emitente (33 = Rio de Janeiro)

  @Column(name = "id_nota_api")
  private String cNF; // Código numérico que compõe a chave de acesso (8 dígitos aleatórios)

  private String natOp = "Venda ao Consumidor"; // Natureza da operação

  private String mod = "65"; // Modelo do documento fiscal (65 = NFC-e)

  private Integer serie; // Série da nota (ex.: 1, definida pelo emitente)

  @Column(name = "numero_nf")
  private Long nNF; // Número da nota fiscal (sequencial por série)

  @Column(name = "data_hora_emissao")
  private LocalDateTime dhEmi; // Data e hora de emissão (ex.: 2025-06-04T21:28:00)

  @Column(name = "tipo_de_operacao")
  private Integer tpNF = 1; // Tipo de operação (1 = saída, padrão para NFC-e)

  @Column(name = "identificador_destino")
  private Integer idDest = 1; // Identificador de destino (1 = operação interna, RJ)

  @Column(name = "codigo_municipio_fato_gerador")
  private Integer cMunFG; // Código do município do fato gerador (ex.: 3304557 = Rio de Janeiro)

  @Column(name = "formato_impressao")
  private Integer tpImp = 4; // Formato de impressão (4 = DANFE NFC-e em impressora não fiscal)

  @Column(name = "tipo_emissao")
  private Integer tpEmis = 1; // Tipo de emissão (1 = normal, 9 = contingência offline)

  @Column(name = "codigo_verificador")
  private Integer cDV; // Dígito verificador da chave de acesso

  @Column(name = "tipo_ambiente")
  private Integer tpAmb = 2; // Ambiente (1 = produção, 2 = homologação)

  @Column(name = "finalidade_emissao")
  private Integer finNFe = 1; // Finalidade da emissão (1 = normal)

  @Column(name = "data_hora_contingencia")
  private LocalDateTime dhCont; // Data/hora da contingência (obrigatório se tpEmis = 9)

  @Column(name = "justificativa_contingencia")
  private String xJust; // Justificativa da contingência (obrigatório se tpEmis = 9)

  private String token_fiscal;

  @Column(name = "valor_total_produtos")
  private Double vProd; // Valor total dos produtos (soma de vProd dos itens)

  @Column(name = "valor_total_nota")
  private Double vNF; // Valor total da nota (igual a vProd, sem acréscimos no Simples Nacional)

  private Double desconto;

  @Column(name = "informacoes", length = 2000)
  private String infCpl; // Informações complementares ao consumidor (ex.: "Emitida em contingência")

  @Column(columnDefinition = "TEXT")
  private String qrCode; // URL do QR Code para consulta da NFC-e
  private String chave; // Chave de acesso da NFC-e (44 dígitos)
  private String protocolo; // Número do protocolo de autorização da SEFAZ
  private String status;

  @Column(name = "motivo_status")
  private String motivo_status;

  @ManyToOne
  @JoinColumn(name = "empresa_id")
  private Empresa empresa; // Referência à empresa emitente (padaria)

  @ManyToOne
  @JoinColumn(name = "destinatario_id")
  private Destinatario destinatario; // Cliente (opcional, para CPF ou entregas)

  @OneToMany(mappedBy = "id.nfce", cascade = CascadeType.ALL)
  private Set<ItemNfce> itens = new HashSet<>(); // Lista de itens da NFC-e

  public List<Product> getProducts() {
    return itens.stream().map(x -> x.getProduct()).toList();
  }

  @ManyToOne
  @JoinColumn(name = "pagamento_id")
  private Pagamento pagamento;

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Nfce other = (Nfce) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

}