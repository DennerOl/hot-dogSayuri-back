package com.erp.pdv.dto.nfe.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.erp.pdv.dto.DestinatarioDTO;
import com.erp.pdv.dto.EmpresaDTO;
import com.erp.pdv.dto.PagamentoDTO;
import com.erp.pdv.dto.nfce.response.AutorizacaoDTO;
import com.erp.pdv.dto.nfe.ItemNfeDTO;
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
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos que não existem no DTO
public class NfeResponseDTO {

  private String id;
  private String ambiente;

  @JsonProperty("created_at")
  private String createdAt;

  @JsonProperty("status")
  private String status;

  @JsonProperty("motivo_status")
  private String motivoStatus;

  @JsonProperty("data_emissao")
  private LocalDateTime dataEmissao;
  private String modelo;
  private Integer serie;

  @JsonProperty("tipo_emissao")
  private Integer tipo;

  @JsonProperty("valor_total")
  private Double valorTotalNota;

  @JsonProperty("chave_acesso")
  private String chave;

  @JsonProperty("autorizacao")
  private AutorizacaoDTO autorizacao;

  @JsonProperty("data_evento")
  private String dataEvento;

  @JsonProperty("numero_sequencial")
  private Long numeroSequencial;

  @JsonProperty("data_recebimento")
  private String dataRecebimento;

  @JsonProperty("numero_protocolo")
  private String protocolo;

  @JsonProperty("mensagem")
  private String mensagemSefaz;

  @JsonProperty("codigo_mensagem")
  private Integer codigo;

  @JsonProperty("url_chave")
  private String urlChave;

  private List<ItemNfeDTO> itens = new ArrayList<>();

  private Double quantidadeTotalItens;

  private Double subtotal;
  private Double desconto;

  private PagamentoDTO pagamentos;

  private String observacao;

  private DestinatarioDTO destinatario;
  private EmpresaDTO empresa;

}
