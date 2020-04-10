package com.codetogether.user;

import java.util.UUID;

import javax.inject.Inject;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.codetogether.login.LoginDTO;


@Controller
@RequestMapping("/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Inject
	private UserService service;
	@Inject
	private JavaMailSender mailSender;


	// 회원 CREATE
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create() throws Exception {
		return "/user/create";
	}
	// 회원 CREATE
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ModelAndView create(UserVO vo) throws Exception {

		ModelAndView mav = new ModelAndView();

		// 비밀번호, 비밀번호 확인이 같은지 체크
		if(!vo.password.equals(vo.re_password)) {
			mav.addObject("msg", "비밀번호 재입력 오류");
			mav.setViewName("/loginForm");
			return mav;
		}

		String hashedPw = BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt()); // BCrypt + salting
		vo.setPassword(hashedPw);

		service.create(vo); //회원정보를 DB에 입력 ( valid = 0 )

		mav.addObject("msg", "회원등록 완료, 이메일 인증을 해주세요");
		mav.setViewName("redirect:/login");

		// 인증 메일 발송
		MailVO sendMail = new MailVO(mailSender);
		sendMail.setSubject("[이메일 인증] CodeTogether에서 신규가입을 환영합니다.");
		sendMail.setText((new StringBuffer().append("<h1>메일인증</h1>")
				.append("<a href='http://localhost:8080/user/verify?email=" + vo.getEmail())
				.append("' target='_blenk'>이메일 인증 확인</a>").toString()));
		sendMail.setFrom("admin@CodeTogether.com", "코드투게더");
		sendMail.setTo(vo.getEmail());
		sendMail.send();

		return mav;
	}


	// 회원정보 조회 GET
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public String select() {
		return "/user/select";
	}
	// 회원정보 조회
	@RequestMapping(value = "/select", method = RequestMethod.POST)
	public ModelAndView select(@RequestParam("email") LoginDTO dto ) throws Exception {

		ModelAndView mav = new ModelAndView();
		UserVO UserInfo = service.select(dto);

		mav.addObject("UserInfo", UserInfo);
		mav.setViewName("/user/select");

		return mav;
	}


	// 회원정보 수정 GET
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String update() {
		return "/user/update";
	}
	// 회원정보 수정 POST
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ModelAndView update(UserVO vo) throws Exception {
		ModelAndView mav = new ModelAndView();

		String hashedPw = BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt());
		vo.setPassword(hashedPw);

		service.update(vo);

		mav.addObject("msg", "회원정보 수정 완료");
		mav.setViewName("/");

		return mav;
	}


	// 회원 DELETE GET
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String delete() {
		return "/user/delete";
	}
	// 회원 DELETE POST
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ModelAndView delete(UserVO vo) throws Exception {
		ModelAndView mav = new ModelAndView();

		String hashedPw = BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt());
		vo.setPassword(hashedPw);

		service.delete(vo);

		mav.addObject("msg", "회원탈퇴 완료");

		return mav;
	}

	// 비밀번호 찾기 GET
	@RequestMapping(value = "/findPassword", method = RequestMethod.GET)
	public String findPassword() throws Exception {
		return "/user/findPassword";
	}

	// 비밀번호 찾기 POST
	@RequestMapping(value = "/findPassword", method = RequestMethod.POST)
	public String forgetPasswordP(UserVO vo) throws Exception {

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


		return "/";

	}

	@RequestMapping(value = "/verify", method = RequestMethod.GET)
	public String verify(@RequestParam @PathVariable String email) {
		System.out.println("이메일 인증기능 처리");


		UserVO uservo = new UserVO();
		uservo.setEmail(email);
		service.verify(uservo);
		return "/user/verify";
	}


	public String tempPassword() {

		String uuid = UUID.randomUUID().toString().replaceAll("-","");
		uuid = uuid.substring(0, 10);

		return uuid;
	}
}


	// 회원 수정 POST

		/*
			if(StringUtils.hasText(userVO.getPassword())) {

			String bCryptString=bCryptPasswordEncoder.encode(userVO.getPassword());
			userVO.setPassword(bCryptString);
		}
			userservice.create(userVO);
		return "signup";
		*/
