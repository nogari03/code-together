package com.codetogether.auth;

import io.jsonwebtoken.UnsupportedJwtException;

public class UnauthorizedException extends RuntimeException {

	public UnauthorizedException() {
		super("계정 권한이 유효하지 않습니다, /n 다시 로그인 해주세요.");
	}

public class UnsupportedJwtException extends RuntimeException {

	public UnsupportedJwtException() {
		super("토큰 구성이 올바르지 않습니다.");
	}
}
}
