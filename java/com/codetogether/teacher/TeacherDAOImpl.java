package com.codetogether.teacher;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.codetogether.user.UserVO;

@Repository
public class TeacherDAOImpl implements TeacherDAO {

	private static final Logger logger = LoggerFactory.getLogger("default");

	@Inject
	private SqlSession sql;

	@Override
	public void transTeacher(UserVO vo) throws Exception {
		try{
			sql.update("trans_teacher", vo);
		} catch (NullPointerException npe) {
			throw new NullPointerException("NullPointerException");
		} catch (Exception e) {
			throw new Exception("알수없는 오류");
		}
	}

	@Transactional
	@Override
	public void createTeacherInfo(TeacherVO tvo) {
		sql.insert("createTeacherInfo",tvo);

	}

	@Override
	public TeacherVO selectTeacherInfo(TeacherVO tvo) throws Exception {

		try{
			sql.selectOne("selectTeacherInfo", tvo);
		} catch (Exception e) {
			throw new Exception();
		}

		return sql.selectOne("selectTeacherInfo", tvo);
	}

	@Transactional
	@Override
	public void updateTeacherInfo(TeacherVO tvo) {
		sql.update("updateTeacherInfo",tvo);
	}

}
