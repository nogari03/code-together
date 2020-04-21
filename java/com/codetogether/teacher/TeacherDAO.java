package com.codetogether.teacher;

import com.codetogether.user.UserVO;

public interface TeacherDAO {

	void transTeacher(UserVO vo) throws Exception;

	void createTeacherInfo(TeacherVO tvo);

	TeacherVO selectTeacherInfo(TeacherVO tvo) throws Exception;

	void updateTeacherInfo(TeacherVO tvo);
}
