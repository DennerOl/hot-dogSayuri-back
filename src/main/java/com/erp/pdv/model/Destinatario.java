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
@Table(name = "tb_destinatario")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Destinatario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 11)
  private String cpf;
  @Column(length = 14)
  private String cnpj;
  @Column(name = "X_NOME", length = 60)
  private String x_nome;
  @Column(length = 60)
  private String logradouro;
  @Column(length = 10)
  private String numero;
  @Column(length = 60)
  private String bairro;
  private Integer cMun;
  @Column(length = 2)
  private String uf;
  @Column(length = 8)
  private String cep;
  private Integer indIEDest = 9; // 9 = Não contribuinte
  private String email;
  private Boolean ativo;

  @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL)
  private List<Nfce> nfces = new ArrayList<>();

}
