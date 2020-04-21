package com.codetogether.mail;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.codetogether.user.UserService;
import com.codetogether.user.UserVO;

@Controller
public class MailController {

	@Inject
	private UserService uService;
	@Inject
	private MailService mService;

	@RequestMapping(value = "/findPassword", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView findPassword(@RequestBody UserVO vo) throws Exception {				// 비밀번호 찾기 메일링.
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		mService.UserSignupMail(vo);

		mav.addObject("result","1");
		mav.addObject("message", "비밀번호 찾기 메일 송신 성공!");

		return mav;

	}

	@RequestMapping(value = "/verify", method = RequestMethod.GET)
	public ModelAndView verify(@RequestParam @PathVariable String email) {
		ModelAndView mav = new ModelAndView();

		UserVO uservo = new UserVO();
		uservo.setEmail(email);
		uService.verify(uservo);
		mav.setViewName("redirect:http://192.168.1.93:8000/");

		return mav;
	}
}
