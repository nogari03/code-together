package com.codetogether.auth;

import java.util.Map;

import com.codetogether.login.LoginDTO;

public interface JwtService {

	public String createToken(LoginDTO dto);

	String ValidToken(String jwt) throws Exception;

	Map<String, Object> getTokenPayload(String jwt);

}
