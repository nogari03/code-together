package com.codetogether.common;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@ControllerAdvice
public class Exception extends RuntimeException {

	private static final long serialVersionUID = 1L;


	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = NotFoundException.class)
	public Model handle (NotFoundException e, Model model) {
		model.addAttribute("msg","경로를 찾을 수 없습니다.");
		return model;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = DuplicateKeyException.class)
	public Model DuplicateKey(DuplicateKeyException e, Model model) {
		model.addAttribute("msg", "이미 존재합니다.");
		return model;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = DataIntegrityViolationException.class)
	public Model DataIntegrityViolation (DataIntegrityViolationException e,Model model) {
		model.addAttribute("msg", "데이터베이스 오류! 다시 시도해주세요");
		return model;
	}
}