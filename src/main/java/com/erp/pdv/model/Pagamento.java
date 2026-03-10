package com.erp.pdv.model;

import java.util.ArrayList;
import java.util.List;

import com.erp.pdv.model.nfce.Nfce;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_pagamento")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pagamento {

  @Id
  private Long id;
  private String t_pag;
  private Double v_pag;

  @OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL)
  private List<Nfce> nfces = new ArrayList<>();

}
