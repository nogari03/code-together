package com.codetogether.auth;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import com.codetogether.login.LoginDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
	static final long EXPIRATIONTIME = (1000 * 60 * 10); // 만료기간 10분
	static final String SECRET = "A";

	Log log = LogFactory.getLog(JwtServiceImpl.class);

	// 토큰 생성
	@Override
	public String createToken(LoginDTO dto) {

		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");

		Map<String, Object> payloads = new HashMap<>();
		Date now = new Date();
		now.setTime(now.getTime() + EXPIRATIONTIME);
		payloads.put("exp", now);
		payloads.put("info",dto);


		String jwt = Jwts.builder()
				.setHeader(headers)
				.setClaims(payloads)
				.signWith(SignatureAlgorithm.HS256, SECRET.getBytes())
				.compact();

		return jwt;

	}

	private byte[] generateKey() {
		byte[] key = null;
		try {
			key = SECRET.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			if(log.isInfoEnabled()) {
				e.printStackTrace();
			} else {
				log.error("JWT 키 생성 실패 ::: {}" + e.getMessage());
			}
		}
		return key;
	}

	// 토큰 검증
	@Override
	public String ValidToken(String jwt) throws Exception {

		String result = "";

		try {
			Jwts.parser()
					.setSigningKey(this.generateKey())
					.parseClaimsJws(jwt)
					.getBody();
			result = "토큰 검증 완료. 유효한 토큰입니다.";

		} catch (UnauthorizedException e) {
			result = "인증되지 않은 토큰 입니다.";
		} catch (ExpiredJwtException eje) {
			result = "만료된 토큰입니다.";
		} catch (SignatureException se) {
			result = "서명이 유효하지 않습니다.";
		}

		return result;
}
	// 토큰에서 값 가져오기
	@Override
	public Map<String, Object> getTokenPayload(String jwt){
		Map<String, Object> payloadMap = new HashMap<>();
		ObjectMapper om = new ObjectMapper();
		String encodedTokenPayload = jwt.split("\\.")[1];
		String tokenPayload = new String(new Base64(true).decode(encodedTokenPayload));
		try {
			payloadMap=om.readValue(tokenPayload, new TypeReference<Map<String, Object>>(){});
		}catch(Exception e) {
			System.out.println("[getTokenPayload] + " +e.getMessage());
		}
		return payloadMap;
	}
}
