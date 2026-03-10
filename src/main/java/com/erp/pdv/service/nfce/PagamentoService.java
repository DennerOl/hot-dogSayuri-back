package com.erp.pdv.service.nfce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.pdv.dto.PagamentoDTO;
import com.erp.pdv.model.Pagamento;
import com.erp.pdv.repository.PagamentoRepository;
import com.erp.pdv.service.exceptions.ResourceNotFoundException;
import com.erp.pdv.utils.Mapper;

@Service
public class PagamentoService {
  @Autowired
  private Mapper pagamentoMapper;

  @Autowired
  private PagamentoRepository repository;

  @Transactional(readOnly = true)
  public PagamentoDTO findById(Long id) {
    Pagamento result = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado "));
    return pagamentoMapper.toDTO(result);
  }
}
