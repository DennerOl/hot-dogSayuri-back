package com.erp.pdv.repository.nfe;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.pdv.model.nfe.NfeSerie;

public interface NfeSerieRepository extends JpaRepository<NfeSerie, Long> {

  Optional<NfeSerie> findFirstByOrderBySerieDesc();

}
