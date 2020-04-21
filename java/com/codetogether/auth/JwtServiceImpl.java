package com.codetogether.auth;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
	static final long EXPIRATIONTIME = (1000 * 60 * 30); // 만료기간 (30분)
	static final String SECRET = "thisissparta";

	Log log = LogFactory.getLog(JwtServiceImpl.class);

	// refresh token 미사용 및 만료시간만 확인할것
	@Override
	public String createToken(String member_id) {

		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");

		Map<String, Object> payloads = new HashMap<>();
		Date now = new Date();
		now.setTime(now.getTime() + EXPIRATIONTIME);
		payloads.put("exp", now);
		payloads.put("member_id",member_id);


		String jwt = null;

			jwt = Jwts.builder()
					.setHeader(headers)
					.setClaims(payloads)
					.signWith(SignatureAlgorithm.HS256, this.generateKey())
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

	//여기서부터 하자
	@Override
	public boolean ValidToken(String token) throws Exception {

		try {
					Jwts.parser()
					.setSigningKey(this.generateKey())
					.parseClaimsJws(token)
					.getBody();

					return true;
			}catch(ExpiredJwtException eje){
				log.debug("JWT 유효기간 초과");
				throw new RuntimeException("JWT 유효기간 초과");
			}catch(UnsupportedJwtException uje){
				log.debug("JWT 형식 불일치");
				throw new RuntimeException("JWT 형식 불일치");
			}catch(MalformedJwtException mje){
				log.debug("잘못된 JWT 구성");
				throw new RuntimeException("잘못된 JWT 구성");
			}catch(SignatureException se){
				log.debug("JWT 서명 확인 불가");
				throw new RuntimeException("JWT 서명 확인 불가");
			}catch(IllegalArgumentException iae){
				log.debug("JWT IllegalArgumentException");
				throw new RuntimeException("JWT IllegalArgumentException");
			}catch (Exception e) {
				log.debug("알 수 없는 오류 발생", e);
				throw new RuntimeException("알 수 없는 오류 발생", e);
			}
}

	@Override
	public Map<String, Object> getTokenPayload(String token){
		Map<String, Object> payloadMap = new HashMap<>();
		ObjectMapper om = new ObjectMapper();
		String encodedTokenPayload = token.split("\\.")[1];
		String tokenPayload = new String(new Base64(true).decode(encodedTokenPayload));
		try {
			payloadMap=om.readValue(tokenPayload, new TypeReference<Map<String, Object>>(){});
		}catch(Exception e) {
			log.debug("페이로드 추출 실패", e);
			throw new RuntimeException("페이로드 추출 실패", e);
		}
		return payloadMap;
	}
}
