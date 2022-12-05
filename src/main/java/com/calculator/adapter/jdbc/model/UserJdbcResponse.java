package com.calculator.adapter.jdbc.model;

import com.calculator.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserJdbcResponse implements Serializable {

	private Integer id;
	private String username;
	private String password;
	private Boolean enabled;

	public User toDomain(){
		return User.builder()
				.id(id)
				.username(username)
				.password(password)
				.enabled(enabled)
				.authority("ROLE_ADMIN")
				.build();
	}

}
