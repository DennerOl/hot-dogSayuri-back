package com.erp.pdv.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.pdv.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

  Optional<Empresa> findByEmitente(Boolean emitente);

  @Query("""
      SELECT e FROM Empresa e
      WHERE e.emitente = :emitente
      AND (:x_nome IS NULL OR e.x_nome LIKE %:x_nome%)
      AND (:cnpj IS NULL OR e.cnpj = :cnpj)
      AND(:ativo IS NULL OR e.ativo = :ativo)
      """)
  Page<Empresa> findAllEmitentes(Pageable pageable, Boolean emitente, String x_nome, String cnpj, Boolean ativo);
}
