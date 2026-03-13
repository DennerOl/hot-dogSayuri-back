package com.erp.pdv.dto.nfce.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NfcePageResponse {
  private List<NfceMinResponse> content;
  private int totalPages;
  private long totalElements;
  private int number;
  private int size;
  private boolean first;
  private boolean last;

  private Double totalValorPeriodo;
}
