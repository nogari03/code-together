package com.codetogether.common;

import java.net.BindException;
import java.sql.SQLException;

import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;


@ControllerAdvice
public class Exception extends RuntimeException {

	private static final Logger logger = LoggerFactory.getLogger("default");

	@ExceptionHandler(value = IllegalStateException.class)
	protected void IllegalStateException (IllegalStateException ise) {
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = NotFoundException.class)
	protected String NotFoundException (NotFoundException nfe, Model model) {
		model.addAttribute(ErrorCode.NOT_FOUND);
		model.addAttribute("result","0");
		model.addAttribute("message","경로를 찾을 수 없습니다.");

		logger.debug("NotFoundException",nfe);

		return "/common/error";
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = BindException.class)
	protected ModelAndView BindException (BindException be) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		mav.addObject(ErrorCode.BIND_EXCEPTION);
		mav.addObject("result", "0");
		mav.addObject("message", "이미 사용중인 주소입니다.");
		logger.debug ("BindException",be);

		return mav;

	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
	protected ModelAndView HttpMediaTypeNotSupported (HttpMediaTypeNotSupportedException hmtnse) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		mav.addObject(ErrorCode.HTTP_MEDIA_TYPE_NOT_SUPPORTED);
		mav.addObject("result", "0");
		mav.addObject("message", "HTTP 미디어타입을 지원하지 않습니다.");
		logger.debug ("HttpMediaTypeNotSupportedException",hmtnse);

		return mav;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = NullPointerException.class)
	protected ModelAndView NullPointerException (NullPointerException npe) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		mav.addObject(ErrorCode.NULL_POINTER_EXCEPTION);
		mav.addObject("result", "0");
		mav.addObject("message", "일치하는 데이터가 없습니다.");
		logger.debug ("NullPointerException",npe);

		return mav;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value =  SQLException.class)
	protected ModelAndView SQLException (SQLException sqle) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		mav.addObject(ErrorCode.SQL_EXCEPTION);
		mav.addObject("result", "0");
		mav.addObject("message", "SQL문이 올바르지 않습니다. 입력값을 확인해주세요.");
		logger.debug("SQLException",sqle);

		return mav;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = Exception.class)
	protected ModelAndView RuntimeException (Exception e) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");

		mav.addObject(ErrorCode.EXCEPTION);
		mav.addObject("result", "0");
		mav.addObject("message", "알 수 없는 오류 발생.");
		logger.error("CommonException",e);

		return mav;
	}

}