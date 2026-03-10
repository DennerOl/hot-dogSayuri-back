package com.erp.pdv.dto.usuario;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;

import com.erp.pdv.model.usuario.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

  private Long id;
  private String name;
  private String email;
  private String phone;
  private String password;

  private List<String> roles = new ArrayList<>();

  public UserDTO(User entity) {
    BeanUtils.copyProperties(entity, this);
    for (GrantedAuthority role : entity.getRoles()) {
      roles.add(role.getAuthority());
    }
  }
}
