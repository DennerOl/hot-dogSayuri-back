package com.erp.pdv.dto.nfe.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.erp.pdv.dto.DestinatarioDTO;
import com.erp.pdv.dto.EmpresaDTO;
import com.erp.pdv.dto.PagamentoDTO;
import com.erp.pdv.dto.nfe.ItemNfeDTO;
import com.erp.pdv.projections.nfe.NfeMinProjection;
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
public class NfeMinResponse {

  private String id;

  private String ambiente;

  private String status;

  @JsonProperty("motivo_status")
  private String motivo_status;

  @JsonProperty("data_emissao")
  private LocalDateTime dataEmissao;

  private Integer serie;

  private Long numero;

  private DestinatarioDTO destinatario;

  private List<EmpresaDTO> emitente = new ArrayList<>();

  @JsonProperty("valor_total")
  private Double valorTotalNota;

  private String chave;

  private List<ItemNfeDTO> itens = new ArrayList<>();

  private Double desconto;

  private List<PagamentoDTO> pagamentos = new ArrayList<>();

  private Integer n_parc;
  private String bandeira;

  public NfeMinResponse(NfeMinProjection projection) {
    this.id = projection.getId().toString();
    this.serie = projection.getSerie();
    this.chave = projection.getChave();
    this.numero = projection.getNumeroNf();
    this.ambiente = projection.getAmbiente();
    this.motivo_status = projection.getMotivoStatus();
    this.n_parc = projection.getPagamentoNParc();
    this.bandeira = projection.getPagamentoBandeira();
    if (projection.getDestinatarioId() != null) {
      DestinatarioDTO destinatario = new DestinatarioDTO();
      destinatario.setId(projection.getDestinatarioId());
      destinatario.setX_nome(projection.getDestinatarioNome());
      destinatario.setCpf(projection.getDestinatarioCpf());
      destinatario.setCnpj(projection.getDestinatarioCnpj());
      this.destinatario = destinatario;
    }
    EmpresaDTO empresa = new EmpresaDTO();
    empresa.setId(projection.getEmitenteId());
    empresa.setX_nome(projection.getEmitenteNome());
    empresa.setCnpj(projection.getEmitenteCnpj());
    this.emitente.add(empresa);

    this.status = projection.getStatus();
    this.valorTotalNota = projection.getValorTotalNota();
    this.dataEmissao = projection.getDataHoraEmissao();
    if (projection.getItemCodigoPrincipal() != null) {
      ItemNfeDTO item = new ItemNfeDTO();
      item.setProductId(projection.getProductId());
      item.setCodigo_principal(projection.getItemCodigoPrincipal());
      item.setDescricao(projection.getItemDescricao());
      item.setQuantidade(projection.getItemQCom());
      item.setPrecoUnitario(projection.getItemPrecoVenda());
      this.itens.add(item);
    }

    // ------ PAGAMENTO ------
    if (projection.getPagamentoTipo() != null) {
      PagamentoDTO pg = new PagamentoDTO();
      pg.setId(projection.getPagId());

      pg.setT_pag(projection.getPagamentoTipo());
      this.pagamentos.add(pg);
    }
  }
}
