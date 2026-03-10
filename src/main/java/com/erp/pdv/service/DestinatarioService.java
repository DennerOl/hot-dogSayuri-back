package com.erp.pdv.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.pdv.dto.DestinatarioDTO;
import com.erp.pdv.model.Destinatario;
import com.erp.pdv.repository.DestinatarioRepository;

@Service
public class DestinatarioService {

  private final ModelMapper modelMapper;

  @Autowired
  private DestinatarioRepository destinatarioRepository;

  DestinatarioService(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Transactional(readOnly = true)
  public DestinatarioDTO findById(Long id) {
    Destinatario destinatario = destinatarioRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Destinatairo não encontrado: " + id));
    return new DestinatarioDTO(destinatario);
  }

  @Transactional
  public DestinatarioDTO insert(DestinatarioDTO destinatarioDTO) {
    Destinatario destinatario = modelMapper.map(destinatarioDTO, Destinatario.class);
    destinatario = destinatarioRepository.save(destinatario);
    return modelMapper.map(destinatario, DestinatarioDTO.class);

  }

  @Transactional
  public DestinatarioDTO findOrCreate(DestinatarioDTO destinatarioDTO) {
    // Se tem ID, busca pelo ID (consumidor final)
    if (destinatarioDTO.getId() != null) {
      return findById(destinatarioDTO.getId());
    }

    // Se tem CPF ou CNPJ, busca ou cria
    if (destinatarioDTO.getCpf() != null || destinatarioDTO.getCnpj() != null) {
      // Busca no banco
      Optional<Destinatario> destinatarioOpt = destinatarioDTO.getCpf() != null
          ? destinatarioRepository.findByCpf(destinatarioDTO.getCpf())
          : destinatarioRepository.findByCnpj(destinatarioDTO.getCnpj());

      // Se encontrou, retorna
      if (destinatarioOpt.isPresent()) {
        return modelMapper.map(destinatarioOpt.get(), DestinatarioDTO.class);
      }

      return insert(destinatarioDTO);
    }

    // Se não tem ID nem CPF/CNPJ, retorna null ou lança exceção
    return null;
  }

  @Transactional(readOnly = true)
  public Page<DestinatarioDTO> findAll(String nome, String cpf, String cnpj, String email, Boolean ativo,
      Pageable pageable) {
    Page<Destinatario> result = destinatarioRepository.search(nome, cpf, cnpj, email, ativo, pageable);
    return result.map(x -> modelMapper.map(x, DestinatarioDTO.class));

  }

}