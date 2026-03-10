package com.erp.pdv.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.pdv.dto.EmpresaDTO;
import com.erp.pdv.model.Empresa;
import com.erp.pdv.repository.EmpresaRepository;
import com.erp.pdv.utils.Mapper;

@Service
public class EmpresaService {

  private final EmpresaRepository empresaRepository;
  private final Mapper empresaMapper;

  public EmpresaService(EmpresaRepository empresaRepository, Mapper empresaMapper) {
    this.empresaRepository = empresaRepository;
    this.empresaMapper = empresaMapper;
  }

  @Transactional(readOnly = true)
  public EmpresaDTO findEmitente(Boolean emitente) {
    Empresa result = empresaRepository.findByEmitente(emitente)
        .orElseThrow(() -> new RuntimeException("Emitente não encontrado."));
    return empresaMapper.toDTO(result);
  }

  @Transactional(readOnly = true)
  public EmpresaDTO findById(long id) {
    Empresa result = empresaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Empresa não encontrada."));
    return empresaMapper.toDTO(result);
  }

  @Transactional(readOnly = true)
  public Page<EmpresaDTO> findAllEmitente(Pageable pageable, Boolean emitente, String x_nome, String cnpj,
      Boolean ativo) {
    Page<Empresa> result = empresaRepository.findAllEmitentes(pageable, emitente, x_nome, cnpj, ativo);
    return result.map(x -> empresaMapper.toDTO(x));

  }

  @Transactional
  public EmpresaDTO insert(EmpresaDTO empresaDTO) {
    Empresa empresa = empresaMapper.toEntity(empresaDTO);
    empresa = empresaRepository.save(empresa);
    return empresaMapper.toDTO(empresa);
  }

  @Transactional
  public EmpresaDTO update(EmpresaDTO dto) {
    Empresa empresa = empresaRepository.getReferenceById(dto.getId());
    empresaMapper.updateEntityFromDTO(dto, empresa);
    empresa = empresaRepository.save(empresa);
    return empresaMapper.toDTO(empresa);
  }

  @Transactional
  public void inativarEmpresa(Long id) {
    Empresa empresa = empresaRepository.getReferenceById(id);
    empresa.setAtivo(false);
    empresaRepository.save(empresa);
  }

}
