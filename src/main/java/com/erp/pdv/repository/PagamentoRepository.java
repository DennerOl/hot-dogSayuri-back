package com.erp.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.pdv.model.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

}
