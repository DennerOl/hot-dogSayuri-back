package com.erp.pdv.dto.nfe;

import java.util.ArrayList;
import java.util.List;

import com.erp.pdv.dto.DestinatarioDTO;
import com.erp.pdv.dto.EmpresaDTO;
import com.erp.pdv.dto.PagamentoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NfeRequestDTO {

  private Long id;
  private List<ItemNfeDTO> itens = new ArrayList<>();
  private Integer qtdeTotalItens;
  private Double valorTotalNota; // Alterado para Double para alinhar com VNF
  private PagamentoDTO pagamentos;
  private Double desconto;
  private DestinatarioDTO destinatario;
  private Integer tpEmis;
  private String xJust;
  private EmpresaDTO empresa;
  @JsonProperty("token_fiscal")
  private String token_fiscal;
  private Integer serie;
  private Long numeroSequencial;
  private Integer n_parc;
  private String bandeira;

}
