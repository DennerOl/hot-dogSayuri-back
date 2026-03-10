package com.erp.pdv.dto.nfe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemNfeDTO {

  private Long productId;
  private String codigo_principal;
  private String descricao;
  private Double quantidade;
  private Double precoUnitario;

}
