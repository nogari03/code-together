package com.codetogether.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;

@Validated
public class UserVO {

	String member_id;

	@Email
	@NotBlank( message = "이메일을 입력해주세요")
	String email;

	@Pattern(regexp="^.*(?=.{6,20})(?=.*[0-9])(?=.*[a-zA-Z]).*$")
	@NotBlank( message = "비밀번호를 입력해주세요")
	String password;

	@Pattern(regexp="^.*(?=.{6,20})(?=.*[0-9])(?=.*[a-zA-Z]).*$")
	@NotBlank( message = "비밀번호 확인을 입력해주세요")
	String re_password;

	@Pattern(regexp = "^[가-힣]{2,6}$")
	@NotBlank( message = "이름을 입력해주세요")
	String name;

	String type;

	@Pattern(regexp = "^(01[1|6|7|8|9|0])-(\\d{3,4})-(\\d{4})$")
	@NotBlank( message = "전화번호를 입력해주세요")
	String phone;

	@Email()
	String naver_email;

	@Email()
	String google_email;

	int valid;

	@Pattern(regexp = "yyyy-mm-dd hh:mm:ss")
	String created_at;

	@Pattern(regexp = "yyyy-mm-dd hh:mm:ss")
	String updated_at;

	String uuid;

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

	public String getRe_password() {
		return re_password;
	}

	public void setRe_password(String re_password) {
		this.re_password = re_password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getNaver_email() {
		return naver_email;
	}

	public void setNaver_email(String naver_email) {
		this.naver_email = naver_email;
	}

	public String getGoogle_email() {
		return google_email;
	}

	public void setGoogle_email(String google_email) {
		this.google_email = google_email;
	}

	public int getValid() {
		return valid;
	}

	public void setValid(int valid) {
		this.valid = valid;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "UserVO [member_id=" + member_id + ", email=" + email + ", password=" + password + ", re_password="
				+ re_password + ", name=" + name + ", type=" + type + ", phone=" + phone + ", naver_email="
				+ naver_email + ", google_email=" + google_email + ", valid=" + valid + ", created_at=" + created_at
				+ ", updated_at=" + updated_at + ", uuid=" + uuid + "]";
	}



}