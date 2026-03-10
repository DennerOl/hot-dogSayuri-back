package com.erp.pdv.dto;

import org.springframework.beans.BeanUtils;

import com.erp.pdv.model.Destinatario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class DestinatarioDTO {
  private Long id;
  private String cpf;
  private String cnpj;
  private String x_nome;
  private String logradouro;
  private String numero;
  private String bairro;
  private Integer cMun;
  private String uf;
  private String cep;
  private Integer indIEDest = 9;
  private String email;
  private Boolean ativo;

  public DestinatarioDTO(Destinatario destinatario) {
    BeanUtils.copyProperties(destinatario, this);

  }
}