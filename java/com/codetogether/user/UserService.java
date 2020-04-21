package com.codetogether.user;

import com.codetogether.login.LoginDTO;

public interface UserService {

	void create(UserVO vo) throws Exception;

	UserVO select(LoginDTO dto) throws Exception;

	void update(UserVO vo) throws Exception;

	void delete(UserVO vo) throws Exception;

	UserVO login(LoginDTO dto) throws Exception;

	UserVO getBySns(UserVO vo) throws Exception;

	void createNaver(UserVO vo) throws Exception;

	int checkValid(String email);

	void verify(UserVO vo);

	void tempPassword(UserVO vo);

	UserVO selectOnlyEmail(UserVO vo);

}
