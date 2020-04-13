package com.codetogether.login;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.codetogether.auth.JwtService;
import com.codetogether.auth.SnsDTO;
import com.codetogether.auth.SnsLogin;
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


	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() throws Exception {
		return "/static/index";
	}

	// 로그인
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ModelAndView loginPOST(@RequestBody LoginDTO dto) throws Exception {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if(service.checkValid(dto.getEmail()) == 0 ) {
			mav.addObject("msg", "유효한 회원이 아닙니다.");
			return mav;
		}

		UserVO userInfo = service.select(dto);

		if (dto == null || !BCrypt.checkpw(dto.getPassword(), userInfo.getPassword())){
			mav.addObject("msg", "비밀번호 입력을 올바르게 해주세요.");
			return mav;
		}

		String token = jwtservice.createToken(dto); //jwt에 이메일 정보 담아서 보내기(test)

		mav.addObject("msg", "로그인에 성공하였습니다!");
		mav.addObject("token",token);
		mav.addObject("member_id",userInfo.getMember_id());

		return mav;
	}

	// 로그아웃 (수정해야함)
	@RequestMapping("/logout.do")
	public String logout(HttpSession session, HttpServletRequest request,HttpServletResponse response) {

		Object obj = session.getAttribute("login");
		if ( obj != null ) {
			//UserVO vo = (UserVO)obj;
			session.removeAttribute("login");
			session.invalidate();

			Cookie cookie = WebUtils.getCookie(request, "cookie");

			if( cookie != null ) {
				cookie.setPath("/");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}

			//여기서 사용자 테이블의 유효기간 현재시간으로 다시 세탕하는 로직 생성
		}
		return "redirect:/";
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

		if ( uservo == null) {

			mav.addObject("result", "SNS로그인! 추가정보를 입력해 주세요.");
			mav.addObject("","");
			mav.addObject(snsUser.getNaver_email()); //
			mav.setViewName("/user/snsForm");
			return mav;

		} else {
			mav.addObject("result", "로그인 완료! 반갑습니다.");
		}

		mav.setViewName("/");
		return mav;
	}

}


// 세션 및 쿠키 -> JWT 사용으로 주석
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
