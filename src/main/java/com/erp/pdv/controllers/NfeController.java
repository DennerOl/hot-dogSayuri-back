package com.erp.pdv.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erp.pdv.dto.nfe.NfeRequestDTO;
import com.erp.pdv.dto.nfe.NfeSerieDTO;
import com.erp.pdv.dto.nfe.response.NfeMinResponse;
import com.erp.pdv.dto.nfe.response.NfeResponseDTO;
import com.erp.pdv.service.nfe.NfeSerieService;
import com.erp.pdv.service.nfe.NfeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/nfe")
@Slf4j
public class NfeController {

  @Autowired
  private NfeSerieService nfeSerieService;
  @Autowired
  private NfeService nfeService;

  @PostMapping()
  public ResponseEntity<NfeResponseDTO> saveNfeSefaz(@RequestBody NfeRequestDTO dto) {

    NfeResponseDTO dtoMin = nfeService.processarNfe(dto);
    return ResponseEntity.ok()
        .body(dtoMin);
  }

  @GetMapping("/notas")
  public ResponseEntity<Page<NfeMinResponse>> findAllNfce(
      @RequestParam(name = "minDate", required = false, defaultValue = "") String minDate,
      @RequestParam(name = "maxDate", required = false, defaultValue = "") String maxDate,
      @RequestParam(name = "cpf", required = false, defaultValue = "") String cpf,
      @RequestParam(name = "cnpj", required = false, defaultValue = "") String cnpj,
      @PageableDefault(size = 5) Pageable pageable) {

    Page<NfeMinResponse> list = nfeService.findAllNfce(cnpj, minDate, maxDate, cpf, pageable);
    return ResponseEntity.ok(list);
  }

  @GetMapping("/{id}")
  public ResponseEntity<NfeMinResponse> findNfeById(@PathVariable Long id) {
    NfeMinResponse dto = nfeService.findNfeById(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/save")
  public ResponseEntity<Void> saveNfe(@RequestBody NfeRequestDTO dto) {
    nfeService.insertNfe(dto);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{id}")
  public ResponseEntity<NfeMinResponse> updateNfe(@PathVariable Long id, @RequestBody NfeRequestDTO dto) {
    NfeMinResponse updatedNfe = nfeService.update(id, dto);
    return ResponseEntity.ok(updatedNfe);
  }

  @GetMapping("/ultimo/numero")
  public NfeSerieDTO getUltimoNumero() {
    return nfeSerieService.getUltimoNumero();
  }

  @GetMapping("/sequencia/proxima")
  public NfeSerieDTO getProximaSequencia() {
    return nfeSerieService.getProximaSequencia();
  }
}
