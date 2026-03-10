package com.erp.pdv.repository.nfce;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.pdv.model.nfce.NfceSerieEmpresa3;

public interface NfceSerieEmpresa3Repository extends JpaRepository<NfceSerieEmpresa3, Long> {

  Optional<NfceSerieEmpresa3> findFirstByOrderBySerieDesc();

}
