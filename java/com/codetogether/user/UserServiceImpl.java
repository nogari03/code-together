package com.codetogether.user;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.codetogether.login.LoginDTO;

@Service
public class UserServiceImpl implements UserService {

	@Inject
	private UserDAO userdao;

	// 회원 가입
	@Override
	public void create(UserVO userVO) throws Exception {
		userdao.create(userVO);
	}

	@Override
	public UserVO select(LoginDTO loginDTO) throws Exception {
		return userdao.select(loginDTO);
	}
	// 회원 수정
	@Override
	public void update(UserVO userVO) throws Exception {
		userdao.update(userVO);
	}

	// 회원 탈퇴
	@Override
	public void delete(UserVO userVO) throws Exception {
		userdao.delete(userVO);

	}

	// 로그인
	@Override
	public UserVO login(LoginDTO loginDTO) throws Exception {
		return userdao.login(loginDTO);
	}

	// 로그아웃 -> JWT 로그아웃으로 ..
	@Override
	public void logout(HttpSession httpsession) {
		httpsession.invalidate();
	}

	@Override
	public UserVO getBySns(UserVO userVO) throws Exception {
		return userdao.getBySns(userVO);
	}

	@Override
	public void verify(UserVO uservo) {
		userdao.verify(uservo);

	}

	@Override
	public int checkValid(String email) {
		if(userdao.checkValid(email) == 0) {
			return 0;
		}
			return 1;
	}

	@Override
	public void tempPassword(UserVO vo) {
		userdao.tempPassword(vo);
	}

}