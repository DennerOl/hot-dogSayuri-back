package com.erp.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.pdv.model.usuario.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

  Role findByAuthority(String authority);

}
