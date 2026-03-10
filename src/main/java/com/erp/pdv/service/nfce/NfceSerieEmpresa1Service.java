package com.erp.pdv.service.nfce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.pdv.dto.nfce.NfceSerieDTO;
import com.erp.pdv.model.nfce.NfceSerieEmpresa1;
import com.erp.pdv.repository.nfce.NfceSerieEmpresa1Repository;

@Service
public class NfceSerieEmpresa1Service extends NfceSerieGenericService<NfceSerieEmpresa1, NfceSerieEmpresa1Repository> {

  @Autowired
  private NfceSerieEmpresa1Repository nfceSerieEmpresa1Repository;

  @Override
  protected NfceSerieEmpresa1 findFirstByOrderBySerieDesc() {
    return nfceSerieEmpresa1Repository.findFirstByOrderBySerieDesc().orElse(null);
  }

  @Override
  protected NfceSerieDTO toDTO(NfceSerieEmpresa1 entity) {
    return mapper.toDTOEmp1(entity);
  }

  @Override
  protected NfceSerieEmpresa1 novaEntidade() {
    return new NfceSerieEmpresa1();
  }

  @Override
  protected int getSerie(NfceSerieEmpresa1 entity) {
    return entity.getSerie();
  }

  @Override
  protected void setSerie(NfceSerieEmpresa1 entity, int serie) {
    entity.setSerie(serie);
  }

  @Override
  protected int getUltimoNumeroValue(NfceSerieEmpresa1 entity) {
    return entity.getUltimoNumero();
  }

  @Override
  protected void setUltimoNumero(NfceSerieEmpresa1 entity, int numero) {
    entity.setUltimoNumero(numero);
  }
}