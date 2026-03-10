package com.erp.pdv.service.nfce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.pdv.dto.nfce.NfceSerieDTO;
import com.erp.pdv.model.nfce.NfceSerieEmpresa3;
import com.erp.pdv.repository.nfce.NfceSerieEmpresa3Repository;

@Service
public class NfceSerieEmpresa3Service extends NfceSerieGenericService<NfceSerieEmpresa3, NfceSerieEmpresa3Repository> {

  @Autowired
  private NfceSerieEmpresa3Repository nfceSerieEmpresa3Repository;

  @Override
  protected NfceSerieEmpresa3 findFirstByOrderBySerieDesc() {
    return nfceSerieEmpresa3Repository.findFirstByOrderBySerieDesc().orElse(null);
  }

  @Override
  protected NfceSerieDTO toDTO(NfceSerieEmpresa3 entity) {
    return mapper.toDTOEmp3(entity);
  }

  @Override
  protected NfceSerieEmpresa3 novaEntidade() {
    return new NfceSerieEmpresa3();
  }

  @Override
  protected int getSerie(NfceSerieEmpresa3 entity) {
    return entity.getSerie();
  }

  @Override
  protected void setSerie(NfceSerieEmpresa3 entity, int serie) {
    entity.setSerie(serie);
  }

  @Override
  protected int getUltimoNumeroValue(NfceSerieEmpresa3 entity) {
    return entity.getUltimoNumero();
  }

  @Override
  protected void setUltimoNumero(NfceSerieEmpresa3 entity, int numero) {
    entity.setUltimoNumero(numero);
  }
}