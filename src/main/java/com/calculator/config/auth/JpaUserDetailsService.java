package com.calculator.config.auth;

import com.calculator.adapter.jdbc.model.RoleJdbcResponse;
import com.calculator.adapter.jdbc.model.UserJdbcResponse;
import com.calculator.application.port.out.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService{

	private final UserRepository usuarioDao;


	public JpaUserDetailsService(UserRepository usuarioDao) {
		this.usuarioDao = usuarioDao;
	}

	
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		com.calculator.domain.User user = usuarioDao.findByUsername(username);
		
		if(user == null) {
			log.error("Error en el login: no existe el usuario: '{}' ", username);
			throw new UsernameNotFoundException(String.format("Username %s no existe en el sistema!", username));
		}
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		authorities.add(new SimpleGrantedAuthority(user.getAuthority()));
		
		if (authorities.isEmpty()) {
			log.error("Error en el login: el usuario: '{}' no tiene roles asignados", username);
			throw new UsernameNotFoundException(String.format("Error en el login: el usuario: '%s' no tiene roles asignados", username));
		}
		return new User(user.getUsername(),user.getPassword(),user.getEnabled(),true,true,true,authorities);
	}

}
