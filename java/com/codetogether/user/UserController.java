package com.codetogether.user;

import java.util.UUID;

import javax.inject.Inject;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.codetogether.auth.SnsDTO;
import com.codetogether.common.ErrorCode;
import com.codetogether.login.LoginDTO;


@Controller
@RequestMapping("/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService service;
	@Autowired
	private JavaMailSender mailSender;
	@Inject
	private SnsDTO naverSns;

	// 회원 가입
	@RequestMapping(value = "/create.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView create(@RequestBody UserVO vo) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");


		if(vo.getEmail() == null || vo.getPassword() == null || vo.getPassword() == null || vo.getName() == null || vo.getPhone() == null) {
			mav.addObject("result", "빈칸을 채워주세요");
			return mav;
		}
		// 비밀번호, 비밀번호 확인이 같은지 체크
		if(!vo.password.equals(vo.re_password)) {
			mav.addObject("result", "비밀번호 재입력 오류");
			return mav;
		}

		String hashedPw = BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt()); // BCrypt + salting
			vo.setPassword(hashedPw);

		try{
			service.create(vo);

		} catch (DataIntegrityViolationException e) {
			mav.addObject("result", "이미 등록된 이메일 입니다.");
			mav.addObject(ErrorCode.EMAIL_DUPLICATION);
			logger.info("DataIntegrityViolationException",e);
			return mav;

		} catch (Exception e) {
			mav.addObject("result", "등록 오류, 다시 시도해주세요");
			mav.addObject(ErrorCode.EXCEPTION);
			logger.info("Exception",e);
			return mav;
		}


		// 인증 메일 발송
		try {
		MailVO sendMail;
		sendMail = new MailVO(mailSender);
		sendMail.setSubject("[이메일 인증] CodeTogether에서 신규가입을 환영합니다.");
		sendMail.setText((new StringBuffer().append("<h1>메일인증</h1>")
				.append("<a href='http://localhost:8080/user/verify?email=" + vo.getEmail())
				.append("' target='_blenk'>이메일 인증 확인</a>").toString()));
		sendMail.setFrom("admin@CodeTogether.com", "코드투게더");
		sendMail.setTo(vo.getEmail());
		sendMail.send();
		} catch (Exception e){
			mav.addObject("result", "인증이메일 전송 오류");
			mav.addObject(ErrorCode.EXCEPTION);
		}

		mav.addObject("result", "회원등록 완료, 이메일 인증을 해주세요");
		return mav;
	}

	// 회원 조회
	@RequestMapping(value = "/select.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView select(@RequestBody LoginDTO dto) throws Exception {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		UserVO UserInfo;
		try {

			UserInfo = service.select(dto);

			if (!BCrypt.checkpw(dto.getPassword(), UserInfo.getPassword())){

				mav.addObject("result", "비밀번호가 일치하지 않습니다.");
				return mav;
			}

		} catch (NullPointerException e) {
			mav.addObject("result","조회 결과가 없습니다.");
			return mav;
		} catch (Exception e) {
			mav.addObject("result","불러오기 실패.");
			logger.info("Exception",e);
			return mav;
		}
		mav.addObject("UserInfo", UserInfo);
		mav.addObject("result","회원정보 불러오기 완료.");

		return mav;
	}

	// 회원 수정
	@RequestMapping(value = "/update.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView update(@RequestBody UserVO vo) throws Exception {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		String hashedPw = BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt());
		vo.setPassword(hashedPw);

		service.update(vo);

		mav.addObject("result", "회원정보 수정 완료");

		return mav;
	}

	// 회원 삭제
	@RequestMapping(value = "/delete.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView delete(@RequestBody LoginDTO dto) throws Exception {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		UserVO UserInfo = service.select(dto);
		if (!BCrypt.checkpw(dto.getPassword(), UserInfo.getPassword())){

			mav.addObject("result", "비밀번호가 일치하지 않습니다.");
			return mav;
		}


		try{
			service.delete(UserInfo);
		} catch (NullPointerException e) {

		}

		mav.addObject("msg", "회원탈퇴 완료");

		return mav;
	}

	// 비밀번호 찾기
	@RequestMapping(value = "/findPassword.do", method = RequestMethod.POST)
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

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = DuplicateKeyException.class)
	protected String DuplicateKey(DuplicateKeyException e, Model model) {
		model.addAttribute(ErrorCode.EMAIL_DUPLICATION);
		logger.info("DuplicateKeyException", e.getMessage());
		return "/common/error";
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = DataIntegrityViolationException.class)
	protected String DataIntegrityViolation (DataIntegrityViolationException e,Model model) {
		model.addAttribute(ErrorCode.DATA_INTEGRITY_VIOLATION);
		logger.info("DataIntegrityViolationException", e);
		return "/common/error";
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
