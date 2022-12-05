package com.calculator.application.port.out;

import com.calculator.domain.User;


public interface UserRepository {
	User findByUsername(String username);
	User save(User user);
}
