package com.calculator.adapter.jdbc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleJdbcResponse implements Serializable {

	private Long id;
	private String authority;
	private UserJdbcResponse user;

}
