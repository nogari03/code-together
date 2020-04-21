package com.codetogether.mail;

import java.util.UUID;

import javax.inject.Inject;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.codetogether.common.ErrorCode;
import com.codetogether.user.UserService;
import com.codetogether.user.UserVO;


@Service
public class MailServiceImpl implements MailService {

	private static final Logger logger = LoggerFactory.getLogger("default");

	static final String MAILING_ADDRESS = "<a href='http://192.168.1.53:8080/user/verify?email=";

	@Autowired
	private JavaMailSender mailSender;

	@Inject
	private UserService service;

	@Override
	public void UserSignupMail(UserVO vo) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

	try {
		MailVO sendMail;
		sendMail = new MailVO(mailSender);
		sendMail.setSubject("[이메일 인증] CodeTogether에서 신규가입을 환영합니다.");
		sendMail.setText((new StringBuffer().append("<h1>메일인증</h1>")
				.append( MAILING_ADDRESS + vo.getEmail())
				.append("' target='_blenk'>이메일 인증 확인</a>").toString()));
		sendMail.setFrom("admin@CodeTogether.com", "코드투게더");
		sendMail.setTo(vo.getEmail());
		sendMail.send();
		} catch (Exception e){

			mav.addObject(ErrorCode.EXCEPTION);
			mav.addObject("result","0");
			mav.addObject("message", "인증이메일 전송 오류");
			logger.error("이메일 전송 오류",e);
		}
	}

	@Override
	public void FindPasswordMail(UserVO vo) throws Exception {

		String uuid = tempPassword();
		String hashedPw = BCrypt.hashpw(uuid, BCrypt.gensalt());
		vo.setPassword(hashedPw);

		service.tempPassword(vo);

		MailVO sendMail = new MailVO(mailSender);
		sendMail.setSubject("[비밀번호 찾기] CodeTogether 임시비밀번호 발급");
		sendMail.setText((new StringBuffer().append("<h1>임시 비밀번호</h1>")
				.append("임시 비밀번호는 "+ uuid )
				.append(" 입니다").toString()));
		sendMail.setFrom("admin@CodeTogether.com", "코드투게더");
		sendMail.setTo(vo.getEmail());
		sendMail.send();
	}

	@Override
	public String tempPassword() {

		String uuid = UUID.randomUUID().toString().replaceAll("-","");
		uuid = uuid.substring(0, 10);

		return uuid;
	}

}
