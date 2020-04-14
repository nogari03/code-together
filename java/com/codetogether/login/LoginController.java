package com.codetogether.login;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

import com.codetogether.auth.JwtService;
import com.codetogether.auth.SnsDTO;
import com.codetogether.auth.SnsLogin;
import com.codetogether.common.ErrorCode;
import com.codetogether.user.UserController;
import com.codetogether.user.UserService;
import com.codetogether.user.UserVO;

@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);


	@Autowired
	private UserService service;
	@Autowired
	private JwtService jwtservice;
	@Inject
	private SnsDTO naverSns;


	// 로그인
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ModelAndView loginPOST(@RequestBody LoginDTO dto) throws Exception {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if(dto.getEmail() == null || dto.getPassword() == null) {
			mav.addObject("result","0");
			mav.addObject("error", "입력값이 올바르지 않습니다.");
			return mav;
		}

		try{
			if(service.checkValid(dto.getEmail()) == 0) {
				mav.addObject("result","0");
				mav.addObject("error", "유효한 회원이 아닙니다.");
				return mav;
			}
		} catch (NullPointerException e) {
			mav.addObject("result","0");
			mav.addObject("error", "회원이 존재하지 않습니다.");
			return mav;
		}

		UserVO userInfo = service.select(dto);

		if (!BCrypt.checkpw(dto.getPassword(), userInfo.getPassword())){
			mav.addObject("result","0");
			mav.addObject("error", "비밀번호가 일치하지 않습니다.");
			return mav;
		}

		String token = jwtservice.createToken(dto.getEmail());

		mav.addObject("success", "로그인에 성공하였습니다!");
		mav.addObject("result","1");
		mav.addObject("token",token);
		mav.addObject("member_id",userInfo.getMember_id());
		logger.info("info","user login");

		return mav;
	}

	// 로그아웃 (수정해야함)
	@RequestMapping("/logout.do")
	@ResponseStatus(HttpStatus.OK)
	public void logout(String token) {
		// 장고단에서 쿠키를 삭제하도록 지시

	}

	@RequestMapping(value = "/auth/{snsService}/callback")
	@ResponseBody
	public ModelAndView naverCallback(@PathVariable String snsService,
		   @RequestParam String code, HttpSession session) throws Exception {

		logger.info("snsLoginCallback: service={}", snsService);

		SnsDTO sns = naverSns;
		snsService.equalsIgnoreCase("naver");

		SnsLogin snsLogin = new SnsLogin(sns);
		UserVO snsUser = snsLogin.getUserProfile(code);
		System.out.println("Profile>>" + snsUser);

		UserVO uservo = service.getBySns(snsUser);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if ( uservo == null) {

			mav.addObject("result", "SNS로그인감지! 추가정보를 입력해 주세요. 다음부터는 자동으로 로그인 됩니다.");
			mav.addObject("naver_email",snsUser.getNaver_email()); //
			return mav;

		} else {

			String token = jwtservice.createToken(snsUser.getNaver_email());
			mav.addObject("token",token);
			mav.addObject("result", "로그인 완료! 반갑습니다.");
		}

		return mav;
	}

	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@RequestMapping(value = "/checkToken.do")
	public int checkToken(@RequestBody String token) throws Exception {

		boolean checkToken = jwtservice.ValidToken(token);

		if (checkToken) {
		return 0; //토큰 유효함
		}
		return 1; //유효하지 않은 토큰
	}

	public Map<String, Object> parseToken(String token){

		Map<String,Object> map = new HashMap();

		try{
			map = jwtservice.getTokenPayload(token);
		} catch (Exception e) {
			logger.info("error","Jwt parse error");
			return null;
		}
		return map;

	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	protected String HttpMessageNotReadable (HttpMessageNotReadableException e, Model model) {
		model.addAttribute(ErrorCode.INVALID_INPUT_VALUE);
		logger.info("HttpMessageNotReadableException", e);
		return "/common/error";
	}

}


// 세션 및 쿠키 -> JWT 사용으로 주석화
//
//session.setAttribute("login", dto);
//
////쿠키사용이 TRUE 일경우
//if( dto.isUseCookie() ) {
//	Cookie cookie = new Cookie("cookie", session.getId());
//	cookie.setPath("/");
//	cookie.setMaxAge(60*60*24*7); //쿠키 시간
//	response.addCookie(cookie);
//	}
//
//mav.addObject("msg", "로그인 성공!");
//mav.setViewName("redirect:/");
//
//return mav;
//}
