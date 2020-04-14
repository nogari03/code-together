package com.codetogether.common;

import java.net.BindException;

import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class Exception extends RuntimeException {

	private static final Logger logger = LoggerFactory.getLogger(Exception.class);

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = NotFoundException.class)
	protected String NotFound (NotFoundException e, Model model) {
		model.addAttribute(ErrorCode.NOT_FOUND);
		logger.error("NotFoundException",e);
		return "/common/error";
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = BindException.class)
	protected String BindException (BindException e, Model model) {
		model.addAttribute(ErrorCode.BIND_EXCEPTION);
		logger.info("BindException",e);
		return "/common/error";

	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = Exception.class)
	protected String Exception (Exception e, Model model) {
		model.addAttribute(ErrorCode.EXCEPTION);
		logger.info("CommonException",e);
		return "/common/error";
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
	protected String HttpMediaTypeNotSupported (HttpMediaTypeNotSupportedException e, Model model) {
		model.addAttribute(ErrorCode.HTTP_MEDIA_TYPE_NOT_SUPPORTED);
		logger.info("HttpMediaTypeNotSupportedException",e);
		return "/common/error";
	}

}