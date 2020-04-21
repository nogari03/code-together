package com.codetogether.user;

import com.codetogether.login.LoginDTO;

public interface UserDAO {

	void create(UserVO vo) throws Exception;			// 유저 생성.
	UserVO select(LoginDTO dto) throws Exception;		// 유저 정보 조회.
	void update(UserVO vo) throws Exception;			// 유저 정보 수정.
	void delete(UserVO vo) throws Exception;			// 유저 삭제.

	UserVO login(LoginDTO dto) throws Exception;		// 로그인.
	int checkValid(String email);						// 회원 유효성 검사.

	UserVO getBySns(UserVO vo);							// SNS 가입 유저 정보 가져오기.
	void createNaver(UserVO vo);						// SNS 가입 유저 추가 정보 입력.

	void verify(UserVO vo);								// 인증 메일링.
	void tempPassword(UserVO vo);						// 임시 비밀번호 발급.

	UserVO selectOnlyEmail(UserVO vo);					// 아이디 중복체크.

}
