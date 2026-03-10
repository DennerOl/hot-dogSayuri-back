package com.erp.pdv.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.pdv.model.Destinatario;

public interface DestinatarioRepository extends JpaRepository<Destinatario, Long> {

  Optional<Destinatario> findByCpf(String cpf);

  Optional<Destinatario> findByCnpj(String cnpj);

  @Query("""
      SELECT obj FROM Destinatario obj
      WHERE (:nome IS NULL OR :nome = '' OR UPPER(obj.x_nome) LIKE UPPER(CONCAT('%', :nome, '%')))
        AND (:cpf IS NULL OR :cpf = '' OR UPPER(obj.cpf) LIKE UPPER(CONCAT('%', :cpf, '%')))
        AND (:cnpj IS NULL OR :cnpj = '' OR UPPER(obj.cnpj) LIKE UPPER(CONCAT('%', :cnpj, '%')))
        AND (:email IS NULL OR :email = '' OR UPPER(obj.email) LIKE UPPER(CONCAT('%', :email, '%')))
        AND (:ativo IS NULL OR obj.ativo = :ativo)
        """)
  Page<Destinatario> search(String nome, String cpf, String cnpj, String email, Boolean ativo, Pageable pageable);

}
