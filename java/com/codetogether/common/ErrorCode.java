package com.codetogether.common;

public enum ErrorCode {

	// Common
	INVALID_INPUT_VALUE(400, "C001", "Invalid input value."),
	METHOD_NOT_ALLOWED(405, "C002", "Method not allowed."),
	HANDLE_ACCESS_DENIED(403, "C004", "Handle access denied."),
	NOT_FOUND(404, "C005", "not found page."),
	TIMEOUT_ERROR(408, "C006", "timeout."),
	BIND_EXCEPTION(409, "C007", "Already used."),

	EXCEPTION(400, "C999", "Client error"),

	// Member
	EMAIL_DUPLICATION(400, "M001", "Duplicated email address"),
	LOGIN_INPUT_INVALID(400, "M002", "Login input invalid."),
	UNAUTHORIZED(401, "M003", "Unauthorized"),


	DATA_INTEGRITY_VIOLATION(500, "C001", "Data integrity violation."),
	HTTP_MEDIA_TYPE_NOT_SUPPORTED(501, "C002", "Http Media Type Not Supported."),
	NULL_POINTER_EXCEPTION(500, "C003", "NullPointerException."),
	SQL_EXCEPTION(500, "C004","SQLException")

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