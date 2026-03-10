package com.erp.pdv.service.produto;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.pdv.dto.produto.HistoricoProdutoDTO;
import com.erp.pdv.model.nfce.ItemNfce;
import com.erp.pdv.model.nfce.Nfce;
import com.erp.pdv.model.nfe.ItemNfe;
import com.erp.pdv.model.nfe.Nfe;
import com.erp.pdv.model.produto.HistoricoProduto;
import com.erp.pdv.model.produto.Product;
import com.erp.pdv.repository.produto.HistoricoProdutoRepository;
import com.erp.pdv.repository.produto.ProductRepository;
import com.erp.pdv.utils.Mapper;
import com.erp.pdv.utils.TipoMovimento;

@Service
public class HistoricoProdutoService {

  @Autowired
  private HistoricoProdutoRepository historicoProdutoRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private Mapper mapper;

  public Page<HistoricoProdutoDTO> listarPorProduto(Long productId, Pageable pageable) {
    Page<HistoricoProduto> historicos = historicoProdutoRepository.findByProductIdOrderByCriadoEmDesc(productId,
        pageable);
    return historicos.map(mapper::toDTOHistoricoProduto);
  }

  @Transactional
  public void registrarMovimentoEstoque(Nfce nfce, TipoMovimento tipoMovimento) {
    if (nfce.getItens() == null || nfce.getItens().isEmpty()) {
      throw new IllegalArgumentException("A NFC-e não possui itens para processar o estoque.");
    }

    for (ItemNfce item : nfce.getItens()) {

      Product product = productRepository.findById(item.getProduct().getId())
          .orElseThrow(() -> new IllegalArgumentException(
              "Produto com ID " + item.getProduct().getId() + " não encontrado no estoque."));

      double saldoAnterior = product.getQuantidade() != null ? product.getQuantidade() : 0.0;
      double vendido = item.getQCom() != null ? item.getQCom() : 0.0;

      double saldoAtual;

      switch (tipoMovimento) {
        case VENDA -> saldoAtual = saldoAnterior - vendido;
        case ENTRADA -> saldoAtual = saldoAnterior + vendido;
        default -> saldoAtual = saldoAnterior;
      }

      // Atualiza estoque
      product.setQuantidade(saldoAtual);
      product.calcularValorTotal();

      productRepository.save(product);

      // Registra histórico
      registrarMovimento(
          product,
          tipoMovimento,
          vendido,
          saldoAnterior,
          saldoAtual,
          (tipoMovimento == TipoMovimento.VENDA ? "Venda via NFC-e Nr " : "Cancelamento NFC-e Nr ") + nfce.getNNF(),
          Instant.now());
    }
  }

  @Transactional
  public void registrarMovimentoEstoque(Nfe nfe, TipoMovimento tipoMovimento) {
    if (nfe.getItens() == null || nfe.getItens().isEmpty()) {
      throw new IllegalArgumentException("A NF-e não possui itens para processar o estoque.");
    }

    for (ItemNfe item : nfe.getItens()) {

      Product product = productRepository.findById(item.getProduct().getId())
          .orElseThrow(() -> new IllegalArgumentException(
              "Produto com ID " + item.getProduct().getId() + " não encontrado no estoque."));

      double saldoAnterior = product.getQuantidade() != null ? product.getQuantidade() : 0.0;
      double vendido = item.getQCom() != null ? item.getQCom() : 0.0;

      double saldoAtual;

      switch (tipoMovimento) {
        case VENDA -> saldoAtual = saldoAnterior - vendido;
        case ENTRADA -> saldoAtual = saldoAnterior + vendido;
        default -> saldoAtual = saldoAnterior;
      }

      // Atualiza estoque
      product.setQuantidade(saldoAtual);
      product.calcularValorTotal();

      productRepository.save(product);

      // Registra histórico
      registrarMovimento(
          product,
          tipoMovimento,
          vendido,
          saldoAnterior,
          saldoAtual,
          (tipoMovimento == TipoMovimento.VENDA ? "Venda via NF-e Nr " : "Cancelamento NFC-e Nr ") + nfe.getNNF(),
          Instant.now());
    }
  }

  public void registrarMovimento(Product product, TipoMovimento tipoMovimento, Double quantidade, Double saldoAnterior,
      Double saldoAtual,
      String referencia, Instant criadoEm) {
    HistoricoProduto historico = new HistoricoProduto();
    historico.setProduct(product);
    historico.setTipoMovimento(tipoMovimento);
    historico.setQuantidade(quantidade);
    historico.setSaldoAnterior(saldoAnterior);
    historico.setSaldoAtual(saldoAtual);
    historico.setReferencia(referencia);
    historico.setCriadoEm(criadoEm);
    historicoProdutoRepository.save(historico);
  }
}
