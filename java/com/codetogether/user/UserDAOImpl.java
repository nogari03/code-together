package com.codetogether.user;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.codetogether.login.LoginDTO;
import com.mysql.cj.util.StringUtils;

@Repository
public class UserDAOImpl implements UserDAO {

	private static final Logger logger = LoggerFactory.getLogger("default");

	@Inject
	private SqlSession sql;

	@Transactional
	@Override
	public void create(UserVO vo) throws Exception {
		try{
			sql.insert("create", vo);
		} catch (DataIntegrityViolationException dve) {
			throw new DataIntegrityViolationException("DB 중복값 입력");
		} catch (Exception e) {
			throw new RuntimeException("회원 등록 오류"	);
		}
	}

	@Transactional
	@Override
	public UserVO select(LoginDTO dto) throws Exception {
		try {
			sql.selectOne("select", dto);
		} catch (NullPointerException npe) {
			throw new NullPointerException("NullPointerException");
		} catch (Exception e) {
			throw new Exception("DB 불러오기 오류");
		}
		return sql.selectOne("select", dto);
	}

	@Transactional
	@Override
	public void update(UserVO vo) throws Exception {
		try {
		sql.update("update", vo);
		} catch (NullPointerException npe) {
			throw new NullPointerException("NullPointerException");
		} catch (Exception e) {
			throw new Exception("유저 업데이트 오류");
		}
	}

	@Transactional
	@Override
	public void delete(UserVO vo) throws Exception {
		sql.delete("delete", vo);
	}

	@Transactional
	@Override
	public UserVO login(LoginDTO dto) throws Exception {
		return sql.selectOne("login", dto);
	}

	@Override
	public int checkValid(String email) {
		return sql.selectOne("checkValid", email);
	}

	@Override
	public UserVO getBySns(UserVO vo) {
		if ( !StringUtils.isNullOrEmpty(vo.getNaver_email())) {
			return sql.selectOne("getBySnsNaver", vo.getNaver_email());
		} else {
			return sql.selectOne("getBySnsGoogle", vo.getGoogle_email());
		}
	}

	@Override
	public void createNaver(UserVO vo) {
		sql.insert("createNaver", vo);
	}

	@Override
	public void verify(UserVO vo) {
		sql.selectOne("verfy", vo);
	}

	@Override
	public void tempPassword(UserVO vo) {
		sql.selectOne("tempPassword", vo);
	}

	@Override
	public UserVO selectOnlyEmail(UserVO vo) {
		return sql.selectOne("selectOnlyEmail", vo);
	}

}