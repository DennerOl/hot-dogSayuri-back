package com.erp.pdv.dto;

import com.erp.pdv.model.Pagamento;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // não manda na consulta os campos nulos
public class PagamentoDTO {

  private Long id;
  private String t_pag;
  private Double v_pag;

  public PagamentoDTO(Pagamento pagamento) {
    this.id = pagamento.getId();
    this.t_pag = pagamento.getT_pag();
    this.v_pag = pagamento.getV_pag();
  }
}
