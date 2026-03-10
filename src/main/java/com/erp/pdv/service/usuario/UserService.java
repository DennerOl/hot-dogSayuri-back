package com.erp.pdv.service.usuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.erp.pdv.dto.usuario.UserDTO;
import com.erp.pdv.model.usuario.Role;
import com.erp.pdv.model.usuario.User;
import com.erp.pdv.projections.UserDetailsProjection;
import com.erp.pdv.repository.RoleRepository;
import com.erp.pdv.repository.UserRepository;
import com.erp.pdv.service.exceptions.ResourceNotFoundException;
import com.erp.pdv.utils.CustomUserUtil;
import com.erp.pdv.utils.Mapper;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private Mapper mapper;

	@Autowired
	@Lazy
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CustomUserUtil customUserUtil;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
		if (result.size() == 0) {
			throw new UsernameNotFoundException("Email not found");
		}

		User user = new User();
		user.setEmail(result.get(0).getUsername());
		user.setPassword(result.get(0).getPassword());
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}

		return user;
	}

	/*
	 * Metodo auxiliar que retorna um usuario
	 * que está logado no sistema
	 */
	protected User authenticated() {
		try {
			String username = customUserUtil.getLoggedUsername();
			return repository.findByEmail(username).get();
		} catch (Exception e) {
			throw new UsernameNotFoundException("Email not found");
		}

	}

	@Transactional(readOnly = true)
	public UserDTO getMe() {
		User user = authenticated();
		return new UserDTO(user);
	}

	@Transactional
	public UserDTO criarUser(UserDTO dto) {
		User user = mapper.toEntity(dto);

		if (repository.findByEmail(user.getEmail()).equals(dto.getEmail())) {
			throw new RuntimeException("Email ja cadastrado");
		}

		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		Role role = roleRepository.findByAuthority("ROLE_CLIENTE");
		user.addRole(role);

		user = repository.save(user);
		return mapper.toDTO(user);
	}

	@Transactional
	public UserDTO update(UserDTO dto) {

		User existingUser = repository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

		if (dto.getName() != null && !dto.getName().equals(existingUser.getName())) {
			existingUser.setName(dto.getName());
		}

		if (dto.getPhone() != null && !dto.getPhone().equals(existingUser.getPhone())) {
			existingUser.setPhone(dto.getPhone());
		}

		if (dto.getPassword() != null && !dto.getPassword().isEmpty()
				&& !dto.getPassword().equals(existingUser.getPassword())) {
			existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
		}

		existingUser = repository.save(existingUser);

		return mapper.toDTO(existingUser);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(String email) {

		User user = repository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));

		repository.delete(user);
	}
}
