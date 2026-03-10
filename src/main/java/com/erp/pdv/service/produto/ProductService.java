package com.erp.pdv.service.produto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.erp.pdv.dto.produto.ProductMinDTO;
import com.erp.pdv.model.produto.Product;
import com.erp.pdv.repository.produto.ProductRepository;
import com.erp.pdv.service.exceptions.ResourceNotFoundException;
import com.erp.pdv.utils.Mapper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

  @Autowired
  private Mapper productMapper;

  @Autowired
  private ProductRepository repository;

  @Transactional(readOnly = true)
  public ProductMinDTO findById(Long id) {
    Product result = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado "));
    return productMapper.toDTO(result);
  }

  @Transactional(readOnly = true)
  public Page<ProductMinDTO> findAll(String nome, String codigo, String cdEan, Boolean ativo, Pageable pageable) {

    Page<Product> result = repository.search(nome, codigo, cdEan, ativo, pageable);
    return result.map(x -> productMapper.toDTO(x));
  }

  @Transactional
  public ProductMinDTO insert(ProductMinDTO dto) {
    Product entity = productMapper.toEntity(dto);
    entity = repository.save(entity);
    return productMapper.toDTO(entity);
  }

  @Transactional
  public ProductMinDTO update(ProductMinDTO dto) {
    try {
      Product entity = repository.getReferenceById(dto.getId());
      productMapper.copyDtoToEntity(dto, entity);
      entity = repository.save(entity);
      return productMapper.toDTO(entity);
    } catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException("Recurso não encontrado " + dto.getId());
    }

  }

  @Transactional(propagation = Propagation.SUPPORTS)
  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Recurso não encontrado " + id);
    }
    try {
      repository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
      throw new RuntimeException("Não foi possível excluir o recurso " + id);
    }
  }

}
