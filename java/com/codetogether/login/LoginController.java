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
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

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
	private GoogleConnectionFactory googleConnectionFactory;
	@Autowired
	private OAuth2Parameters googleOAuth2Parameters;
	@Inject
	private SnsDTO naverSns;
	@Inject
	private SnsDTO googleSns;


	// 로그인 페이지
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() throws Exception {
		return "/loginForm";
	}

	//로그인 (폼데이터)
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	public ModelAndView loginPOST(LoginDTO dto, HttpServletResponse response, HttpSession session) throws Exception {
		ModelAndView mav = new ModelAndView();

		// 유효한 회원인지 ( valid = 1 ) 검증
		if(service.checkValid(dto.getEmail()) == 0 ) {
			// valid가 0인관계로 로그인 실패
			mav.addObject("msg", "유효한 회원이 아닙니다.");
			mav.setViewName("redirect:/loginForm");
			//mav.setStatus(""); //스테이터스 같이 보낼것
			return mav;
		}

		//로그인
		UserVO userPw = service.select(dto);

		// 비밀번호 검증
		if (dto == null || !BCrypt.checkpw(dto.getPassword(), userPw.getPassword())){
			mav.addObject("msg", "비밀번호가 입력을 올바르게 해주세요.");
			mav.setViewName("redirect:/loginForm");
			return mav;
		}

		// 세션값을 부여
		session.setAttribute("login", dto);

		//쿠키사용이 TRUE 일경우
		if( dto.isUseCookie() ) {
			Cookie cookie = new Cookie("cookie", session.getId());
			cookie.setPath("/");
			cookie.setMaxAge(60*60*24*7); //쿠키 시간
			response.addCookie(cookie);
			}

		mav.addObject("msg", "로그인 성공!");
		mav.setViewName("redirect:/");

		return mav;
		}

	// 로그아웃
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
	public String naverCallback(@PathVariable String snsService,
			Model model, @RequestParam String code, HttpSession session) throws Exception {

		logger.info("snsLoginCallback: service={}", snsService);

		SnsDTO sns = null;
		if (snsService.equalsIgnoreCase("naver")) {
			sns = naverSns;
		} else {
			sns = googleSns;
		}

		SnsLogin snsLogin = new SnsLogin(sns);
		UserVO snsUser = snsLogin.getUserProfile(code);
		System.out.println("Profile>>" + snsUser);

		UserVO uservo = service.getBySns(snsUser);

		if ( uservo == null) {
			model.addAttribute("result", "존재하지 않는 사용자입니다. 가입해 주세요.");

		} else {
			model.addAttribute("result", uservo.getName() + "님 반갑습니다.");
			session.setAttribute("login", uservo);


		}

		return "/login/loginResult";
	}

	// 로그인 폼
	@RequestMapping(value = "/loginForm")
	public String loginForm(Model model) throws Exception {

		SnsLogin naverLogin = new SnsLogin(naverSns);
		model.addAttribute("naver_url", naverLogin.getNaverAuthURL());

		OAuth2Operations oauthOperations = googleConnectionFactory.getOAuthOperations();
		String url = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE,googleOAuth2Parameters);

		model.addAttribute("google_url", url);
		return "/login/loginForm";
	}
}
