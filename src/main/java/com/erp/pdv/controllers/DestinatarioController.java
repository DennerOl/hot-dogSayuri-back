package com.erp.pdv.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.pdv.dto.DestinatarioDTO;
import com.erp.pdv.service.DestinatarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/cliente")
public class DestinatarioController {

    @Autowired
    private DestinatarioService destinatarioService;

    @GetMapping
    public ResponseEntity<Page<DestinatarioDTO>> findAll(
            @RequestParam(name = "nome", defaultValue = "") String nome,
            @RequestParam(name = "cpf", defaultValue = "") String cpf,
            @RequestParam(name = "cnpj", defaultValue = "") String cnpj,
            @RequestParam(name = "email", defaultValue = "") String email,

            @RequestParam(name = "ativo", required = false) Boolean ativo,
            Pageable pageable) {
        return ResponseEntity.ok(destinatarioService.findAll(nome, cpf, cnpj, email, ativo, pageable));

    }

}
