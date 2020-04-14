package com.codetogether.user;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.codetogether.login.LoginDTO;
import com.mysql.cj.util.StringUtils;

@Repository
public class UserDAOImpl implements UserDAO {

	@Inject
	private SqlSession sql;

	// 회원 가입
	@Override
	public void create(UserVO userVO) throws Exception {
		sql.insert("create", userVO);
	}

	@Override
	public UserVO select(LoginDTO loginDTO) throws Exception {
		return sql.selectOne("select", loginDTO);
	}
	// 회원 수정
	@Override
	public void update(UserVO userVO) throws Exception {
		sql.update("update", userVO);
	}

	// 회원 탈퇴
	@Override
	public void delete(UserVO userVO) throws Exception {
		sql.delete("delete", userVO);
	}

	// 로그인 처리
	@Override
	public UserVO login(LoginDTO loginDTO) throws Exception {
		return sql.selectOne("login", loginDTO);

	}

	// 로그 아웃
	@Override
	public void logout(HttpSession httpsession) {

	}
	@Override
	public int checkValid(String email) {
		return sql.selectOne("checkValid", email);
	}

	@Override
	public UserVO getBySns(UserVO userVO) {
		if ( !StringUtils.isNullOrEmpty(userVO.getNaver_email())) {
			return sql.selectOne("getBySnsNaver", userVO.getNaver_email());
		} else {
			return sql.selectOne("getBySnsGoogle", userVO.getGoogle_email());
	}
}
	@Override
	public void createNaver(UserVO vo) {
		sql.insert("createNaver", vo);
	}

	@Override
	public void verify(UserVO uservo) {
		sql.selectOne("verfy", uservo);
	}

	@Override
	public void tempPassword(UserVO vo) {
		sql.selectOne("tempPassword", vo);
	}
}