package com.dev.finances.service;

import java.util.Optional;

import com.dev.finances.api.dto.UserAuthenticated;
import com.dev.finances.model.entity.User;

public interface UserService {

	UserAuthenticated auth(String email, String password);
	
	User save(User user);

	void validateEmail(String email);
	
	Optional<User> getById(Long id);

	User findByEmail(String email);
}
