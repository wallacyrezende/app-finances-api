package com.dev.finances.service.impl;

import java.util.Optional;

import com.dev.finances.api.dto.UserAuthenticated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.finances.exception.AuthenticationException;
import com.dev.finances.exception.BusinessException;
import com.dev.finances.model.entity.User;
import com.dev.finances.repository.UserRepository;
import com.dev.finances.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	private UserRepository repository;
	
	public UserServiceImpl(UserRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public UserAuthenticated auth(String email, String password) {
		Optional<User> user = repository.findByEmail(email);
		
		if(!user.isPresent()) {
			throw new AuthenticationException("Usuário não encontrado para o e-mail informado.");
		}
		
		if(!user.get().getPassword().equals(password)) {
			throw new AuthenticationException("Senha inválida.");
		}

		return UserAuthenticated.builder()
				.id(user.get().getId())
				.name(user.get().getName())
				.email(user.get().getEmail())
				.build();
	}

	@Override
	public User findByEmail(String email) {
		Optional<User> user = repository.findByEmail(email);

		if(!user.isPresent()) {
			throw new BusinessException("Usuário não encontrado para o e-mail informado.");
		}
		return User.builder()
				.id(user.get().getId())
				.name(user.get().getName())
				.email(user.get().getEmail())
				.password(user.get().getPassword())
				.roles(user.get().getRoles())
				.build();
	}

	@Override
	@Transactional
	public User save(User user) {
		validateEmail(user.getEmail());
		return repository.save(user);
	}

	@Override
	public void validateEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if(existe) {
			throw new BusinessException("Já existe um usuário cadastrado com este email.");
		}
	}

	@Override
	public Optional<User> getById(Long id) {
		return repository.findById(id);
	}

}
