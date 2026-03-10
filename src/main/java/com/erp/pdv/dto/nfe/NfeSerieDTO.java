package com.erp.pdv.dto.nfe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NfeSerieDTO {

  private Long id;
  private Integer serie;
  private Integer ultimoNumero;
}
