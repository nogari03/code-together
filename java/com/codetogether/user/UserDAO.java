package com.codetogether.user;

import com.codetogether.login.LoginDTO;

public interface UserDAO {

	public void create(UserVO vo) throws Exception;

	UserVO select(LoginDTO dto) throws Exception;

	public void update(UserVO vo) throws Exception;

	public void delete(UserVO vo) throws Exception;

	UserVO login(LoginDTO dto) throws Exception;

	int checkValid(String email);

	public UserVO getBySns(UserVO vo);

	void createNaver(UserVO vo);

	public void verify(UserVO vo);

	public void tempPassword(UserVO vo);

	public void trans_teacher(UserVO vo) throws Exception;

	UserVO selectOnlyEmail(UserVO vo);

	void createTeacherInfo(TeacherVO tvo);

	TeacherVO selectTeacherInfo(TeacherVO tvo) throws Exception;

	void updateTeacherInfo(TeacherVO tvo);



}
