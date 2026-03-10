package com.erp.pdv.dto.nfce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NfceSerieDTO {

  private Long id;
  private Integer serie;
  private Integer ultimoNumero;
}
