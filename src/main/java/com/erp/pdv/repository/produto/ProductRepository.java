package com.erp.pdv.repository.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.pdv.model.produto.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("""
      SELECT obj FROM Product obj
      WHERE (:nome = '' OR UPPER(obj.x_prod) LIKE UPPER(CONCAT('%', :nome, '%')) )
        AND (:codigo = '' OR UPPER(obj.c_prod) LIKE UPPER(CONCAT('%', :codigo, '%')) )
        AND (:cdEan = '' OR UPPER(obj.c_EAN) LIKE UPPER(CONCAT('%', :cdEan, '%')) )
        AND (:ativo IS NULL OR obj.ativo = :ativo)
        """)
  Page<Product> search(String nome, String codigo, String cdEan, Boolean ativo, Pageable pageable);

}