package com.erp.pdv.repository.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.erp.pdv.model.produto.HistoricoProduto;

public interface HistoricoProdutoRepository extends JpaRepository<HistoricoProduto, Long> {

  @Query("SELECT ph FROM HistoricoProduto ph WHERE ph.product.id = :productId ORDER BY ph.criadoEm DESC")
  Page<HistoricoProduto> findByProductIdOrderByCriadoEmDesc(@Param("productId") Long productId, Pageable pageable);

}
