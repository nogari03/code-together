package com.codetogether.teacher;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.codetogether.common.ErrorCode;
import com.codetogether.user.UserVO;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

	private static final Logger logger = LoggerFactory.getLogger("default");

	@Inject
	private TeacherService tService;

	@RequestMapping(value="/transFromUser", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView trans_teacher(@RequestBody UserVO vo) throws Exception {					// 선생님 유저 전환.
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if( vo == null) {

			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result","0");
			mav.addObject("message", "값을 입력해주세요");
			return mav;
		}
		tService.trans_teacher(vo);

		mav.addObject("result", "1");
		mav.addObject("message", "선생님 전환 완료. 이제부터 강의 업로드가 가능합니다.");
		logger.info("#################### 선생님 회원 전환 : 이메일 :{} ####################", vo.getEmail());

		return mav;
	}

	@RequestMapping(value = "/createInfo", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView createTeacherInfo(@RequestBody TeacherVO tvo) throws Exception{			// 선생님 정보 생성.
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if(tvo.getMember_id() == null) {

			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result", "0");
			mav.addObject("message", "선생님 회원이 맞는지 확인해주세요.");
			return mav;
		}

		tvo.setTeacher_id(tvo.getMember_id());
		tService.createTeacherInfo(tvo);

		mav.addObject("result", "1");
		mav.addObject("message", "선생님 정보 등록 완료");
		return mav;
	}

	@RequestMapping(value = "selectInfo", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView selectTeacherInfo(@RequestBody TeacherVO tvo) throws Exception{			// 선생님 정보 조회.
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");


		tvo.setTeacher_id(tvo.getMember_id());

		TeacherVO teacher_info;

		if(tService.selectTeacherInfo(tvo) == null) {

			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result","0");
			mav.addObject("message", "선생님 조회 정보가 없습니다.");
			return mav;
		}

		teacher_info = tService.selectTeacherInfo(tvo);

		mav.addObject("result","1");
		mav.addObject("message", "선생님 정보 조회 완료");
		mav.addObject("teacher_info", teacher_info);
		return mav;
	}

	@RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView updateTeacherInfo(@RequestBody TeacherVO tvo) throws Exception{			// 선생님 정보 수정.
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		tvo.setTeacher_id(tvo.getMember_id());

		if(tService.selectTeacherInfo(tvo) == null) {

			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result","0");
			mav.addObject("message", "선생님 조회 정보가 없습니다.");
			return mav;

		}

		tService.updateTeacherInfo(tvo);

		mav.addObject("result","1");
		mav.addObject("message", "선생님 정보 수정 완료");
		return mav;
	}
}
