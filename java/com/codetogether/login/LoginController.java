package com.codetogether.login;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.codetogether.auth.JwtService;
import com.codetogether.user.UserService;
import com.codetogether.user.UserVO;

@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger("default");


	@Autowired
	private UserService uService;
	@Inject
	private JwtService jwtService;
	@Inject
	private SnsDTO naverSns;

	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView loginPOST(@RequestBody LoginDTO dto) throws Exception {				// 로그인.

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if(dto.getEmail() == null || dto.getPassword() == null) {
			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject("result","0");
			mav.addObject("message", "입력값이 올바르지 않습니다.");
			return mav;
		}

		try{
			if(uService.checkValid(dto.getEmail()) == 0) {
				mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
				mav.addObject("result","0");
				mav.addObject("message", "유효한 회원이 아닙니다.");
				return mav;
			}
		} catch (NullPointerException e) {
			mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			mav.addObject("result","0");
			mav.addObject("message", "회원이 존재하지 않습니다.");
			return mav;
		}

		UserVO userInfo = uService.select(dto);

		if (!BCrypt.checkpw(dto.getPassword(), userInfo.getPassword())){
			mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			mav.addObject("result","0");
			mav.addObject("message", "비밀번호가 일치하지 않습니다.");
			return mav;
		}

		String token = jwtService.createToken(userInfo.getMember_id());

		mav.addObject("result","1");
		mav.addObject("message", "로그인에 성공하였습니다!");
		mav.addObject("member_id",userInfo.getMember_id());
		mav.addObject("token",token);

		logger.info("############################ 로그인 감지 : 이메일 :{} ###########################",userInfo.getEmail());

		return mav;
	}
	@RequestMapping(value = "/{snsService}/callback", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView naverCallback(@PathVariable String snsService,						// 네이버 콜백 처리.
		   @RequestParam String code, HttpSession session) throws Exception {

		logger.info("snsLoginCallback: service={}", snsService);

		SnsDTO sns = naverSns;
		snsService.equalsIgnoreCase("naver");

		SnsLogin snsLogin = new SnsLogin(sns);
		UserVO snsUser = snsLogin.getUserProfile(code);
		System.out.println("Profile>>" + snsUser);

		UserVO uservo = uService.getBySns(snsUser);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if ( uservo == null) {
			mav.addObject("result", "0");
			mav.addObject("message", "SNS로그인감지! 추가정보를 입력해 주세요. 다음부터는 자동으로 로그인 됩니다.");
			mav.addObject("naver_email",snsUser.getNaver_email()); //
			return mav;

		} else {

			String token = jwtService.createToken(snsUser.getNaver_email());
			mav.addObject("result","1");
			mav.addObject("message", "로그인 완료! 반갑습니다.");
			mav.addObject("token",token);
			logger.info("######################### SNS 로그인 감지 : 이메일 :{} ########################",snsUser.getNaver_email());
		}

		return mav;
	}

}