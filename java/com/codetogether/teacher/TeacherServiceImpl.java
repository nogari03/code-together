package com.codetogether.teacher;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.codetogether.user.UserVO;

@Service
public class TeacherServiceImpl implements TeacherService {

	@Inject
	TeacherDAO tdao;

	@Override
	public void trans_teacher(UserVO vo) throws Exception {
		tdao.transTeacher(vo);
	}

	@Override
	public void createTeacherInfo(TeacherVO tvo) {
		tdao.createTeacherInfo(tvo);
	}

	@Override
	public TeacherVO selectTeacherInfo(TeacherVO tvo) throws Exception {
		return tdao.selectTeacherInfo(tvo);
	}

	@Override
	public void updateTeacherInfo(TeacherVO tvo) {
		tdao.updateTeacherInfo(tvo);
	}

}
