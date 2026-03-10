package com.erp.pdv.dto.nfce.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.erp.pdv.dto.DestinatarioDTO;
import com.erp.pdv.dto.EmpresaDTO;
import com.erp.pdv.dto.PagamentoDTO;
import com.erp.pdv.dto.nfce.ItemNfceDTO;
import com.erp.pdv.projections.NfceMinProjection;
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
public class NfceMinResponse {

  private String id;

  private String ambiente;

  private String status;

  @JsonProperty("motivo_status")
  private String motivo_status;

  @JsonProperty("data_emissao")
  private LocalDateTime dataEmissao;

  private Integer serie;

  private Long numero;

  private List<DestinatarioDTO> autor = new ArrayList<>();

  private List<EmpresaDTO> emitente = new ArrayList<>();

  @JsonProperty("valor_total")
  private Double valorTotalNota;

  private String chave;

  private List<ItemNfceDTO> itens = new ArrayList<>();

  private Double desconto;

  private List<PagamentoDTO> pagamentos = new ArrayList<>();

  public NfceMinResponse(NfceMinProjection projection) {
    this.id = projection.getId().toString();
    this.serie = projection.getSerie();
    this.chave = projection.getChave();
    this.numero = projection.getNumeroNf();
    this.ambiente = projection.getAmbiente();
    this.motivo_status = projection.getMotivoStatus();
    if (projection.getDestinatarioId() != null) {
      DestinatarioDTO destinatario = new DestinatarioDTO();
      destinatario.setId(projection.getDestinatarioId());
      destinatario.setX_nome(projection.getDestinatarioNome());
      destinatario.setCpf(projection.getDestinatarioCpf());
      destinatario.setCnpj(projection.getDestinatarioCnpj());
      this.autor.add(destinatario);

      EmpresaDTO empresa = new EmpresaDTO();
      empresa.setId(projection.getEmitenteId());
      empresa.setX_nome(projection.getEmitenteNome());
      empresa.setCnpj(projection.getEmitenteCnpj());
      this.emitente.add(empresa);
    }

    this.status = projection.getStatus();
    this.valorTotalNota = projection.getValorTotalNota();
    this.dataEmissao = projection.getDataHoraEmissao();
    if (projection.getItemCodigoPrincipal() != null) {
      ItemNfceDTO item = new ItemNfceDTO();
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
