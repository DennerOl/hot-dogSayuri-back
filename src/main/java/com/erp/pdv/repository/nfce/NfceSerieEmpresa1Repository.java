package com.erp.pdv.repository.nfce;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.pdv.model.nfce.NfceSerieEmpresa1;

public interface NfceSerieEmpresa1Repository extends JpaRepository<NfceSerieEmpresa1, Long> {

  Optional<NfceSerieEmpresa1> findFirstByOrderBySerieDesc();

}
