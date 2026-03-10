package com.erp.pdv.model.nfe;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nfe_serie", uniqueConstraints = @UniqueConstraint(columnNames = { "serie" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NfeSerie {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Integer serie;

  @Column(name = "ultimo_numero")
  private Integer ultimoNumero;
}
