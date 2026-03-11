package com.erp.pdv.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.erp.pdv.dto.nfce.NfceRequestDTO;
import com.erp.pdv.dto.nfce.NfceSerieDTO;
import com.erp.pdv.dto.nfce.response.NfceMinResponse;
import com.erp.pdv.dto.nfce.response.NfceResponseDTO;
import com.erp.pdv.service.nfce.NfceLoteService;
import com.erp.pdv.service.nfce.NfceSerieEmpresa1Service;
import com.erp.pdv.service.nfce.NfceSerieEmpresa2Service;
import com.erp.pdv.service.nfce.NfceSerieEmpresa3Service;
import com.erp.pdv.service.nfce.NfceService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/nfce")
@Slf4j
public class NfceController {

  @Autowired
  private NfceService nfceService;

  @Autowired
  private NfceLoteService nfceLoteService;

  @Autowired
  private NfceSerieEmpresa1Service nfceSerieEmpresa1Service;

  @Autowired
  private NfceSerieEmpresa2Service nfceSerieEmpresa2Service;

  @Autowired
  private NfceSerieEmpresa3Service nfceSerieEmpresa3Service;

  @PostMapping()
  public ResponseEntity<NfceResponseDTO> saveNfceSefaz(@RequestBody NfceRequestDTO dto) {

    NfceResponseDTO dtoMin = nfceService.processarNfce(dto);
    return ResponseEntity.ok()
        .body(dtoMin);
  }

  @PostMapping("/enviar-lote")
  public ResponseEntity<Void> enviarLoteSefaz(@RequestBody List<Long> ids, @RequestParam String token) {
    nfceLoteService.enviarLoteNfce(ids, token);
    return ResponseEntity.ok().build();

  }

  @GetMapping("/{id}/segunda-via")
  public ResponseEntity<NfceResponseDTO> imprimirSegViaNfce(@PathVariable Long id) {
    NfceResponseDTO response = nfceLoteService.findByIdNfce(id);

    return ResponseEntity.ok()
        .body(response);
  }

  @PostMapping("/cancelar")
  public ResponseEntity<Void> cancelarNfce(@RequestBody List<Long> ids, @RequestParam String token) {
    nfceLoteService.cancelarNfce(ids, token);
    return ResponseEntity.ok().build();

  }

  @PostMapping("/save/orcamento")
  public ResponseEntity<NfceResponseDTO> saveOrcamento(@RequestBody NfceRequestDTO dto) {
    NfceResponseDTO dtoMin = nfceService.insertOrcamento(dto);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dtoMin.getId()).toUri();
    return ResponseEntity.created(uri).body(dtoMin);
  }

  @GetMapping("/notas")
  public ResponseEntity<Page<NfceMinResponse>> findAllNfce(
      @RequestParam(name = "minDate", required = false, defaultValue = "") String minDate,
      @RequestParam(name = "maxDate", required = false, defaultValue = "") String maxDate,
      @RequestParam(name = "destinatarioId", required = false) Long destinatarioId,
      @RequestParam(name = "emitenteId", required = false) Long emitenteId,
      @PageableDefault(size = 10) Pageable pageable) {

    Page<NfceMinResponse> list = nfceService.findAllNfce(emitenteId, minDate, maxDate, destinatarioId, pageable);
    return ResponseEntity.ok(list);
  }

  // --- Empresa 1 ---
  @GetMapping("/empresa1/ultimo/numero")
  public NfceSerieDTO getUltimoNumeroEmpresa1() {
    return nfceSerieEmpresa1Service.getUltimoNumero();
  }

  @GetMapping("/empresa1/sequencia/proxima")
  public NfceSerieDTO getProximaSequenciaEmpresa1() {
    return nfceSerieEmpresa1Service.getProximaSequencia();
  }

  // --- Empresa 2 ---
  @GetMapping("/empresa2/ultimo/numero")
  public NfceSerieDTO getUltimoNumeroEmpresa2() {
    return nfceSerieEmpresa2Service.getUltimoNumero();
  }

  @GetMapping("/empresa2/sequencia/proxima")
  public NfceSerieDTO getProximaSequenciaEmpresa2() {
    return nfceSerieEmpresa2Service.getProximaSequencia();
  }

  // --- Empresa 3 ---
  @GetMapping("/empresa3/ultimo/numero")
  public NfceSerieDTO getUltimoNumeroEmpresa3() {
    return nfceSerieEmpresa3Service.getUltimoNumero();
  }

  @GetMapping("/empresa3/sequencia/proxima")
  public NfceSerieDTO getProximaSequenciaEmpresa3() {
    return nfceSerieEmpresa3Service.getProximaSequencia();
  }

}
