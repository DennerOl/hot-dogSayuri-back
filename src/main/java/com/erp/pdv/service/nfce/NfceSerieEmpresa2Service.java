package com.erp.pdv.service.nfce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.pdv.dto.nfce.NfceSerieDTO;
import com.erp.pdv.model.nfce.NfceSerieEmpresa2;
import com.erp.pdv.repository.nfce.NfceSerieEmpresa2Repository;

@Service
public class NfceSerieEmpresa2Service extends NfceSerieGenericService<NfceSerieEmpresa2, NfceSerieEmpresa2Repository> {

  @Autowired
  private NfceSerieEmpresa2Repository nfceSerieEmpresa2Repository;

  @Override
  protected NfceSerieEmpresa2 findFirstByOrderBySerieDesc() {
    return nfceSerieEmpresa2Repository.findFirstByOrderBySerieDesc().orElse(null);
  }

  @Override
  protected NfceSerieDTO toDTO(NfceSerieEmpresa2 entity) {
    return mapper.toDTOEmp2(entity);
  }

  @Override
  protected NfceSerieEmpresa2 novaEntidade() {
    return new NfceSerieEmpresa2();
  }

  @Override
  protected int getSerie(NfceSerieEmpresa2 entity) {
    return entity.getSerie();
  }

  @Override
  protected void setSerie(NfceSerieEmpresa2 entity, int serie) {
    entity.setSerie(serie);
  }

  @Override
  protected int getUltimoNumeroValue(NfceSerieEmpresa2 entity) {
    return entity.getUltimoNumero();
  }

  @Override
  protected void setUltimoNumero(NfceSerieEmpresa2 entity, int numero) {
    entity.setUltimoNumero(numero);
  }
}