package com.erp.pdv.service.nfe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.pdv.dto.nfe.NfeSerieDTO;
import com.erp.pdv.model.nfce.NfceSerieEmpresa1;
import com.erp.pdv.model.nfe.NfeSerie;
import com.erp.pdv.repository.nfe.NfeSerieRepository;
import com.erp.pdv.service.exceptions.ResourceNotFoundException;
import com.erp.pdv.utils.Mapper;

@Service
public class NfeSerieService {

  @Autowired
  private NfeSerieRepository nfeSerieRepository;

  @Autowired
  private Mapper mapper;

  @Transactional
  public synchronized NfeSerieDTO getUltimoNumero() {
    NfeSerie result = nfeSerieRepository.findFirstByOrderBySerieDesc()
        .orElseThrow(() -> new ResourceNotFoundException("Serie não encotrada"));
    return null;// mapper.toDTO(result);
  }

  @Transactional
  public NfeSerieDTO getProximaSequencia() {
    int maxNumero = 999_999_999;
    int maxSerie = 999;
    NfeSerie seq = nfeSerieRepository.findFirstByOrderBySerieDesc()
        .orElseGet(() -> {
          NfeSerie novo = new NfeSerie();
          novo.setSerie(1);
          novo.setUltimoNumero(0);
          return nfeSerieRepository.save(novo);
        });
    int proximoNumero = seq.getUltimoNumero() + 1;
    if (proximoNumero > maxNumero) {
      int novaSerie = seq.getSerie() + 1;
      if (novaSerie > maxSerie) {
        throw new RuntimeException("Limite máximo de série atingido!");
      }
      seq.setSerie(novaSerie);
      proximoNumero = 1;
    }
    seq.setUltimoNumero(proximoNumero);
    nfeSerieRepository.save(seq);
    return null;// mapper.toDTO(seq);
  }
}
