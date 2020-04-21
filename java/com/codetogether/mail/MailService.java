package com.codetogether.mail;

import com.codetogether.user.UserVO;

public interface MailService {

	void UserSignupMail(UserVO vo) throws Exception;

	void FindPasswordMail(UserVO vo) throws Exception;

	String tempPassword();

	}