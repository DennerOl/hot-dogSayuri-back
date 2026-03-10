package com.erp.pdv.dto;

import org.springframework.beans.BeanUtils;

import com.erp.pdv.model.Empresa;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // não manda na consulta os campos nulos
public class EmpresaDTO {
  private Long id;
  private String cnpj;
  private String x_nome;
  private String xFant;
  private String logradouro;
  private String numero;
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

  public EmpresaDTO(Empresa empresa) {
    BeanUtils.copyProperties(empresa, this);
  }
}
