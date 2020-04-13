package com.codetogether.user;

import javax.servlet.http.HttpSession;

import com.codetogether.login.LoginDTO;

public interface UserService {

	// 회원 가입
	void create(UserVO userVO) throws Exception;

	// 회원 조회
	UserVO select(LoginDTO loginDTO) throws Exception;

	// 회원 수정
	void update(UserVO userVO) throws Exception;

	// 회원 탈퇴
	void delete(UserVO userVO) throws Exception;

	// 회원 로그인
	UserVO login(LoginDTO loginDTO) throws Exception;

	// 회원 로그아웃
	public void logout(HttpSession httpsession);

	UserVO getBySns(UserVO userVO) throws Exception;

	//

	// 이메일 1개 가져오기


	// 회원 유효성 검사
	int checkValid(String email);

	void verify(UserVO uservo);

	void tempPassword(UserVO vo);

}
