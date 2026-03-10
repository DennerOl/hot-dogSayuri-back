package com.erp.pdv.repository.nfce;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.pdv.model.nfce.NfceSerieEmpresa2;

public interface NfceSerieEmpresa2Repository extends JpaRepository<NfceSerieEmpresa2, Long> {

  Optional<NfceSerieEmpresa2> findFirstByOrderBySerieDesc();

}
