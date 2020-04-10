package com.codetogether.login;

public class LoginDTO {

	private String email;
	private String password;
	private boolean useCookie;

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
	public boolean isUseCookie() {
		return useCookie;
	}
	public void setUseCookie(boolean useCookie) {
		this.useCookie = useCookie;
	}
	@Override
	public String toString() {
		return "LoginDTO [email=" + email + ", password=" + password + ", useCookie=" + useCookie + ", getEmail()="
				+ getEmail() + ", getPassword()=" + getPassword() + ", isUseCookie()=" + isUseCookie() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
}