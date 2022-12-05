package com.calculator.config.auth;

import com.calculator.adapter.jdbc.model.UserJdbcResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private AuthenticationManager authenticationManager;
	
	
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login","POST"));
	}


	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			{

		String username = obtainUsername(request);
		String password = obtainPassword(request);

		
		if (username!=null && password !=null) {
			logger.info("Username desde request parameter (form-data): "+username);
			logger.info("Password desde request parameter (form-data): "+password);
		}else {
			UserJdbcResponse user;
			try {
			
				user = new ObjectMapper().readValue(request.getInputStream(), UserJdbcResponse.class);
				username = user.getUsername();
				password = user.getPassword();
				logger.info("Username desde request (raw): "+username);
				logger.info("Password desde request (raw): "+password);
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(username != null)
			username = username.trim();
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
		
		return authenticationManager.authenticate(authToken);
	}



	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		String username = ((User) authResult.getPrincipal()).getUsername();
		
		Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
		
		Claims claims = Jwts.claims();
		claims.put("authorities", new ObjectMapper().writeValueAsString(roles));	
		
				
		String token = Jwts.builder().
						setClaims(claims).
						setSubject(username).
						setIssuedAt(new Date()).
						setExpiration(new Date(System.currentTimeMillis() + 14000000L)).
						signWith(SignatureAlgorithm.HS512, "Alguna.clave.secreta.123456".getBytes())
						.compact();
		
		response.addHeader("Authorization", "Bearer "+token);
		
		Map <String, Object> body = new HashMap<>();
		
		body.put("token", token);
		body.put("user", (User) authResult.getPrincipal());
		body.put("mensaje", "Hola "+ username+ ", has iniciado sesion con exito");
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(200);
		response.setContentType("application/json");
		
	}



	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		
		
		Map <String, Object> body = new HashMap<>();	
		
		body.put("mensaje", "Usuario o contraseña incorrecto!");
		body.put("error", failed.getMessage());
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");
		
	}
	
	
	

	
	
	
	
}
