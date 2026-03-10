package com.erp.pdv.controllers.produto;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.erp.pdv.dto.produto.ProductMinDTO;
import com.erp.pdv.service.produto.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

  @Autowired
  private ProductService productService;

  @GetMapping
  public ResponseEntity<Page<ProductMinDTO>> findAll(
      @RequestParam(name = "nome", defaultValue = "") String nome,
      @RequestParam(name = "codigo", defaultValue = "") String codigo,
      @RequestParam(name = "codEan", defaultValue = "") String codEan,

      @RequestParam(name = "ativo", required = false) Boolean ativo,
      @PageableDefault(size = 10) Pageable pageable) {

    return ResponseEntity.ok(productService.findAll(nome, codigo, codEan, ativo, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductMinDTO> findById(@PathVariable Long id) {

    ProductMinDTO dto = productService.findById(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<ProductMinDTO> insert(@RequestBody ProductMinDTO dto) {

    dto = productService.insert(dto);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(dto.getId()).toUri();
    return ResponseEntity.created(uri).body(dto);
  }

  @PatchMapping(value = "/update")
  public ResponseEntity<ProductMinDTO> update(@RequestBody ProductMinDTO dto) {
    dto = productService.update(dto);
    return ResponseEntity.ok().body(dto);

  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
