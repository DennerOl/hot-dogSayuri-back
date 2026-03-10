package com.erp.pdv.model;

import java.util.ArrayList;
import java.util.List;

import com.erp.pdv.model.nfce.Nfce;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_emitente")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Empresa {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String cnpj;
  private String x_nome;
  @Column(length = 60)
  private String xFant;
  private String logradouro;
  private String numero;
  @Column(length = 60)
  private String bairro;
  private String cMun;
  private String xMun;
  private String uf;
  private String cep;
  private String ie;
  private Integer crt = 1; // 1 = Simples Nacional
  private Boolean emitente;
  private Boolean ativo;
  private String email;

  @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
  private List<Nfce> nfces = new ArrayList<>();
}
