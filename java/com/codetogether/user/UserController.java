package com.codetogether.user;

import javax.inject.Inject;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.codetogether.common.ErrorCode;
import com.codetogether.login.LoginDTO;
import com.codetogether.login.SnsDTO;
import com.codetogether.mail.MailService;


@RestController
@RequestMapping("/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger("default");

	@Autowired
	private UserService service;
	@Autowired
	private MailService mService;
	@Inject
	private SnsDTO naverSns;

	@RequestMapping(value = "/checkId", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView checkId(@RequestBody UserVO vo) throws Exception {							// 아이디 유효성 검사.

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if(service.selectOnlyEmail(vo) != null) {
			mav.addObject("result","0");
			mav.addObject("message","사용할 수 없는 아이디 입니다.");
			return mav;
		}
		mav.addObject("result","1");
		mav.addObject("message", "아이디를 사용할 수 있습니다. 계속 진행해주세요.");
		return mav;
	}
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView create(@RequestBody UserVO vo) throws Exception {							// 유저 신규 가입.

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if(!vo.password.equals(vo.re_password)) {

			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject("result", "0");
			mav.addObject("message", "비밀번호 재입력 오류");
			return mav;
		}

		String hashedPw = BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt()); // BCrypt + salting
		vo.setPassword(hashedPw);

		service.create(vo);

		mService.UserSignupMail(vo);

		mav.addObject("result","1");
		mav.addObject("message", "회원등록 완료, 이메일 인증을 해주세요");

		logger.info("#################### 신규회원가입 감지 : 이메일 :{} ####################", vo.getEmail());
		return mav;
	}
	@RequestMapping(value = "/select", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView select(@RequestBody LoginDTO dto) throws Exception {						// 유저 정보 조회.

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		UserVO member_info;
		member_info = service.select(dto);

		if (!BCrypt.checkpw(dto.getPassword(), member_info.getPassword())){

			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result", "0");
			mav.addObject("message", "비밀번호가 일치하지 않습니다.");
			return mav;
		}
		mav.addObject("result","1");
		mav.addObject("message","회원정보 불러오기 완료.");
		mav.addObject("member_info", member_info);


		return mav;
	}
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView update(@RequestBody UserVO vo) throws Exception {							// 유저 정보 수정.

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if(vo.getEmail() == null || vo.getPassword() == null || vo.getName() == null || vo.getName() == null) {
			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result","0");
			mav.addObject("message", "수정할 정보를 올바르게 입력해주세요.");
			return mav;

		}
		String hashedPw = BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt());
		vo.setPassword(hashedPw);

		if(service.selectOnlyEmail(vo) == null) {
			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result","0");
			mav.addObject("message", "회원정보가 없습니다.");
			return mav;
		}

		service.update(vo);

		mav.addObject("result","1");
		mav.addObject("message", "회원정보 수정 완료");

		return mav;
	}
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView delete(@RequestBody LoginDTO dto) throws Exception {						// 유저 탈퇴 처리.

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		UserVO UserInfo = service.select(dto);

		if(!dto.getMember_id().equals(UserInfo.getMember_id())){

			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result", "0");
			mav.addObject("message", "본인 계정만 삭제할 수 있습니다.");
			return mav;

		}

		if (!BCrypt.checkpw(dto.getPassword(), UserInfo.getPassword())){

			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result", "0");
			mav.addObject("message", "비밀번호가 일치하지 않습니다.");
			return mav;
		}

		service.delete(UserInfo);

		mav.addObject("result", "1");
		mav.addObject("message", "회원탈퇴 완료");

		logger.info("#################### 회원 탈퇴 감지 : 이메일 :{} ####################", dto.getEmail());

		return mav;
	}
}