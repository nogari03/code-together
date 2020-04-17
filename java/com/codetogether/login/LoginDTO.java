package com.codetogether.login;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;

@Validated
public class LoginDTO {

	private String member_id;

	@Email
	@NotBlank( message = "이메일을 입력해주세요")
	private String email;

	@Pattern(regexp="^.*(?=.{6,20})(?=.*[0-9])(?=.*[a-zA-Z]).*$")
	@NotBlank( message = "비밀번호를 입력해주세요")
	private String password;

	private String token;

//	private boolean useCookie; //쿠키 미사용




	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

//	public boolean isUseCookie() {
//		return useCookie;
//	}
//
//	public void setUseCookie(boolean useCookie) {
//		this.useCookie = useCookie;
//	}

	@Override
	public String toString() {
		return "LoginDTO [member_id=" + member_id + ", email=" + email + ", password=" + password + ", token=" + token;
	}

}