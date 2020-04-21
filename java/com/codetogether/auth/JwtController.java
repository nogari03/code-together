package com.codetogether.auth;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Controller
@RequestMapping("/auth")
public class JwtController {

	private static final Logger logger = LoggerFactory.getLogger("default");

	@Autowired
	private JwtService jwtservice;

	@ResponseBody
	@RequestMapping(value = "/checkToken", method = RequestMethod.POST)
	public boolean checkToken(@RequestBody String token) throws Exception { 		// token 유효성 검사.

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(token);
		String tokenValue = element.getAsJsonObject().get("token").getAsString();

		boolean checkToken = jwtservice.ValidToken(tokenValue);

		if (checkToken) {
		return true;
		}
		return false;
	}

	public Map<String, Object> parseToken(String token){ 							// token parsing.

		Map<String,Object> map = new HashMap();

		try{
			map = jwtservice.getTokenPayload(token);
		} catch (Exception e) {
			logger.info("error","Jwt parse error");
			return null;
		}
		return map;

	}

	@ResponseBody
	@RequestMapping(value="/getId", method = RequestMethod.POST)
	public ModelAndView getMemberId (@RequestBody String token) throws Exception { 	// token에서 member id 추출.
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if(this.checkToken(token)) {
			Map<String, Object> map = this.parseToken(token);
			mav.addObject("result", "1");
			mav.addObject("member_id", map.get("member_id"));
			return mav;
		}
		mav.addObject("result", "0");
		return mav;
	}

}
