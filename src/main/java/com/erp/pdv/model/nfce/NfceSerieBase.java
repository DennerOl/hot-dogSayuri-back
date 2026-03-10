package com.erp.pdv.model.nfce;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass // Define que os campos pertencem às subclasses
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class NfceSerieBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer serie;

  @Column(name = "ultimo_numero")
  private Integer ultimoNumero;
}
