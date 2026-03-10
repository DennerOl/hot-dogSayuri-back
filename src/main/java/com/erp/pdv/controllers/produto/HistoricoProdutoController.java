package com.erp.pdv.controllers.produto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.pdv.dto.produto.HistoricoProdutoDTO;
import com.erp.pdv.service.produto.HistoricoProdutoService;

@RestController
@RequestMapping("/produtos/{productId}/historicos")
public class HistoricoProdutoController {

  @Autowired
  private HistoricoProdutoService historicoProdutoService;

  @GetMapping
  public ResponseEntity<Page<HistoricoProdutoDTO>> listarPorProduto(
      @PathVariable Long productId,
      Pageable pageable) {
    return ResponseEntity.ok(historicoProdutoService.listarPorProduto(productId, pageable));
  }
}