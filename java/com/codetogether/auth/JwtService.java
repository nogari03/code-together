package com.codetogether.auth;

import java.util.Map;

public interface JwtService {

	public String createToken(String email);

	boolean ValidToken(String token) throws Exception;

	Map<String, Object> getTokenPayload(String jwt);

}
