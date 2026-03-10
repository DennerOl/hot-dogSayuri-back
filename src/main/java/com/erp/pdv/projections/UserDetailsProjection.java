package com.erp.pdv.projections;

public interface UserDetailsProjection {

	String getUsername();

	String getPassword();

	Long getRoleId();

	String getAuthority();

}
