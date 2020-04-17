package com.codetogether.user;

import java.sql.SQLIntegrityConstraintViolationException;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.codetogether.auth.SnsDTO;
import com.codetogether.common.ErrorCode;
import com.codetogether.login.LoginDTO;


@RestController
@RequestMapping("/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger("default");

	@Autowired
	private UserService service;
	@Autowired
	private JavaMailSender mailSender;
	@Inject
	private SnsDTO naverSns;

	@RequestMapping(value = "/checkId.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView checkId(@RequestBody UserVO vo) throws Exception {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if(service.selectOnlyEmail(vo) == null) {
			mav.addObject("result","1");
			mav.addObject("message", "아이디를 사용할 수 있습니다. 계속 진행해주세요.");
			return mav;
		}

		mav.addObject("result","0");
		mav.addObject("message","사용할 수 없는 아이디 입니다.");

		return mav;
	}


	@RequestMapping(value = "/create.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView create(@RequestBody UserVO vo) throws Exception {

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

		try {
		MailVO sendMail;
		sendMail = new MailVO(mailSender);
		sendMail.setSubject("[이메일 인증] CodeTogether에서 신규가입을 환영합니다.");
		sendMail.setText((new StringBuffer().append("<h1>메일인증</h1>")
				.append("<a href='http://192.168.1.53:8080/user/verify?email=" + vo.getEmail())
				.append("' target='_blenk'>이메일 인증 확인</a>").toString()));
		sendMail.setFrom("admin@CodeTogether.com", "코드투게더");
		sendMail.setTo(vo.getEmail());
		sendMail.send();
		} catch (Exception e){

			mav.addObject(ErrorCode.EXCEPTION);
			mav.addObject("result","0");
			mav.addObject("message", "인증이메일 전송 오류");
			logger.error("이메일 전송 오류",e);
		}
		mav.addObject("result","1");
		mav.addObject("message", "회원등록 완료, 이메일 인증을 해주세요");

		logger.info("#################### 신규회원가입 감지 : 이메일 :{} ####################", vo.getEmail());
		return mav;
	}

	@RequestMapping(value = "/select.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView select(@RequestBody LoginDTO dto) throws Exception {

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

	@RequestMapping(value = "/update.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView update(@RequestBody UserVO vo) throws Exception {

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

	@RequestMapping(value = "/delete.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView delete(@RequestBody LoginDTO dto) throws Exception {

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

	@RequestMapping(value = "/findPassword.do", method = RequestMethod.POST)
	public String findPassword(UserVO vo) throws Exception {

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
	public ModelAndView verify(@RequestParam @PathVariable String email) {
		ModelAndView mav = new ModelAndView();

		UserVO uservo = new UserVO();
		uservo.setEmail(email);
		service.verify(uservo);
		mav.setViewName("redirect:http://192.168.1.93:8000/");

		return mav;
	}

	public String tempPassword() {

		String uuid = UUID.randomUUID().toString().replaceAll("-","");
		uuid = uuid.substring(0, 10);

		return uuid;
	}

	@RequestMapping(value="/trans_teacher.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView trans_teacher(@RequestBody UserVO vo) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		if( vo == null) {

			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result","0");
			mav.addObject("message", "값을 입력해주세요");
			return mav;
		}
		service.trans_teacher(vo);

		mav.addObject("result", "1");
		mav.addObject("message", "선생님 전환 완료. 이제부터 강의 업로드가 가능합니다.");
		logger.info("#################### 선생님 회원 전환 : 이메일 :{} ####################", vo.getEmail());

		return mav;
	}


	@RequestMapping(value = "/createTeacherInfo.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView createTeacherInfo(@RequestBody TeacherVO tvo) throws Exception{
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
		service.createTeacherInfo(tvo);

		mav.addObject("result", "1");
		mav.addObject("message", "선생님 정보 등록 완료");
		return mav;
	}


	@RequestMapping(value = "selectTeacherInfo.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView selectTeacherInfo(@RequestBody TeacherVO tvo) throws Exception{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");


		tvo.setTeacher_id(tvo.getMember_id());

		TeacherVO teacher_info;

		if(service.selectTeacherInfo(tvo) == null) {

			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result","0");
			mav.addObject("message", "선생님 조회 정보가 없습니다.");
			return mav;
		}

		teacher_info = service.selectTeacherInfo(tvo);

		mav.addObject("result","1");
		mav.addObject("message", "선생님 정보 조회 완료");
		mav.addObject("teacher_info", teacher_info);
		return mav;
	}



	@RequestMapping(value = "/updateTeacherInfo.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView updateTeacherInfo(@RequestBody TeacherVO tvo) throws Exception{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		tvo.setTeacher_id(tvo.getMember_id());

		if(service.selectTeacherInfo(tvo) == null) {

			mav.setStatus(HttpStatus.BAD_REQUEST);
			mav.addObject(ErrorCode.INVALID_INPUT_VALUE);
			mav.addObject("result","0");
			mav.addObject("message", "선생님 조회 정보가 없습니다.");
			return mav;
		}

		service.updateTeacherInfo(tvo);

		mav.addObject("result","1");
		mav.addObject("message", "선생님 정보 수정 완료");
		return mav;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = DuplicateKeyException.class)
	protected ModelAndView DuplicateKey(DuplicateKeyException dke) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		mav.addObject(ErrorCode.EMAIL_DUPLICATION);
		mav.addObject("result", "0");
		mav.addObject("message", "이미 등록된 회원입니다.");
		logger.debug ("DuplicateKeyException",dke);

		return mav;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = DataIntegrityViolationException.class)
	protected ModelAndView DataIntegrityViolation (DataIntegrityViolationException dive) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		mav.addObject(ErrorCode.DATA_INTEGRITY_VIOLATION);
		mav.addObject("result", "0");
		mav.addObject("message", "데이터베이스 입력 오류 입니다.");
		logger.debug ("DataIntegrityViolationException",dive);

		return mav;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
	protected ModelAndView SQLIntegrityConstraintViolationException (SQLIntegrityConstraintViolationException sqlive) {
		ModelAndView mav = new ModelAndView();
		mav.addObject(ErrorCode.DATA_INTEGRITY_VIOLATION);
		mav.addObject("result", "0");
		mav.addObject("message", "데이터베이스 오류 다시 시도해주세요.");
		logger.debug ("SQLIntegrityConstraintViolationException",sqlive);

		return mav;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = NullPointerException.class)
	protected ModelAndView NullPointerException(NullPointerException npe) {
		ModelAndView mav = new ModelAndView();
		mav.addObject(ErrorCode.NULL_POINTER_EXCEPTION);
		mav.addObject("result", "0");
		mav.addObject("message", "데이터가 없습니다.");
		logger.debug ("NullPointerException",npe);

		return mav;
	}
}