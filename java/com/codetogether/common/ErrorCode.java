package com.codetogether.common;

public enum ErrorCode {

	// Common
	INVALID_INPUT_VALUE(400, "C001", "유효하지 않은 입력값 입니다."),
	METHOD_NOT_ALLOWED(405, "C002", "허용되지 않은 메소드 입니다."),
	HANDLE_ACCESS_DENIED(403, "C004", "접근이 거부되었습니다."),
	NOT_FOUND(404, "C005", "페이지를 찾을 수 없습니다."),
	TIMEOUT_ERROR(408, "C006", "타임아웃 에러."),
	BIND_EXCEPTION(409, "C007", "이미 사용되었습니다."),

	EXCEPTION(400, "C999", "클라이언트 에러"),

	// Member
	EMAIL_DUPLICATION(400, "M001", "중복 이메일 오류 입니다."),
	LOGIN_INPUT_INVALID(400, "M002", "로그인 입력값 오류 입니다."),
	UNAUTHORIZED(401, "M003", "인증되지 않았습니다."),


	DATA_INTEGRITY_VIOLATION(500, "C001", "데이터 형식 위반 오류 입니다."),
	HTTP_MEDIA_TYPE_NOT_SUPPORTED(501, "C002", "Http 미디어 타입을 지원하지 않습니다."),
	NULL_POINTER_EXCEPTION(500, "C003", "널 포인터 오류 입니다."),
	SQL_EXCEPTION(500, "C004","SQL 오류 입니다.")

	;
	private final String code;
	private final String message;
	private int status;

	ErrorCode(final int status, final String code, final String message) {
		this.status = status;
		this.message = message;
		this.code = code;
}
}