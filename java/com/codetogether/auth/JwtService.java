package com.codetogether.auth;

import java.util.Map;

public interface JwtService {

	public String createToken(String email);

	Boolean ValidToken(String jwt) throws Exception;

	Map<String, Object> getTokenPayload(String jwt);

}
