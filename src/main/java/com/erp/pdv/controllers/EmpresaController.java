package com.erp.pdv.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.pdv.dto.EmpresaDTO;
import com.erp.pdv.service.EmpresaService;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

  private final EmpresaService empresaService;

  public EmpresaController(EmpresaService empresaService) {
    this.empresaService = empresaService;
  }

  @GetMapping("/empresas/emitente")
  public ResponseEntity<EmpresaDTO> getEmitente(@PathVariable Boolean emitente) {
    EmpresaDTO empresaDTO = empresaService.findEmitente(emitente);
    return ResponseEntity.ok(empresaDTO);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmpresaDTO> findById(@PathVariable Long id) {
    EmpresaDTO empresaDTO = empresaService.findById(id);
    return ResponseEntity.ok(empresaDTO);
  }

  @GetMapping
  public ResponseEntity<Page<EmpresaDTO>> findAll(Pageable pageable,
      @RequestParam(name = "emitente", required = false) Boolean emitente,
      @RequestParam(name = "x_nome", defaultValue = "") String x_nome,
      @RequestParam(name = "cnpj", defaultValue = "") String cnpj,
      @RequestParam(name = "ativo", required = false) Boolean ativo

  ) {
    return ResponseEntity.ok(empresaService.findAllEmitente(pageable, emitente, x_nome, cnpj, ativo));
  }

}
