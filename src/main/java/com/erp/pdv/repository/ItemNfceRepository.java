package com.erp.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.pdv.model.nfce.ItemNfce;
import com.erp.pdv.model.nfce.ItemNfcePk;

public interface ItemNfceRepository extends JpaRepository<ItemNfce, ItemNfcePk> {

}
