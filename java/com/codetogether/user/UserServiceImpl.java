package com.codetogether.user;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.codetogether.login.LoginDTO;

@Service
public class UserServiceImpl implements UserService {

	@Inject
	private UserDAO userdao;

	@Override
	public void create(UserVO vo) throws Exception {
			userdao.create(vo);
	}

	@Override
	public UserVO select(LoginDTO dto) throws Exception {
		return userdao.select(dto);
	}

	@Override
	public void update(UserVO vo) throws Exception {
			userdao.update(vo);
	}

	@Override
	public void delete(UserVO vo) throws Exception {
		userdao.delete(vo);
	}

	@Override
	public UserVO login(LoginDTO dto) throws Exception {
		return userdao.login(dto);
	}

	@Override
	public UserVO getBySns(UserVO vo) throws Exception {
		return userdao.getBySns(vo);
	}

	@Override
	public void createNaver(UserVO vo) throws Exception {
		userdao.createNaver(vo);
	}
	@Override
	public void verify(UserVO vo) {
		userdao.verify(vo);

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

	@Override
	public void trans_teacher(UserVO vo) throws Exception {
		userdao.trans_teacher(vo);
	}

	@Override
	public UserVO selectOnlyEmail(UserVO vo) {
		return userdao.selectOnlyEmail(vo);
	}

	@Override
	public void createTeacherInfo(TeacherVO tvo) {
		userdao.createTeacherInfo(tvo);
	}

	@Override
	public TeacherVO selectTeacherInfo(TeacherVO tvo) throws Exception {
		return userdao.selectTeacherInfo(tvo);
	}

	@Override
	public void updateTeacherInfo(TeacherVO tvo) {
		userdao.updateTeacherInfo(tvo);
	}

}