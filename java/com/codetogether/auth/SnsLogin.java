package com.codetogether.auth;

import com.codetogether.user.UserVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

public class SnsLogin {

	private OAuth20Service oauthService;
	private SnsDTO sns;

	// 구글로그인 미사용
	public SnsLogin(SnsDTO sns) {
		this.oauthService = new ServiceBuilder(sns.getClientId())
				.apiSecret(sns.getClientSecret())
				.callback(sns.getRedirectUrl())
				.scope("profile")
				.build(sns.getApi20Instance());

		this.sns = sns;
	}

	public String getNaverAuthURL() {
		return this.oauthService.getAuthorizationUrl();
	}


	public UserVO getUserProfile(String code) throws Exception {

		OAuth2AccessToken accessToken = oauthService.getAccessToken(code);
		OAuthRequest request = new OAuthRequest(Verb.GET, this.sns.getProfileUrl());
		oauthService.signRequest(accessToken, request);

		Response response = oauthService.execute(request);
		return parseJson(response.getBody());

	}

	private UserVO parseJson(String body) throws Exception {

		UserVO uservo = new UserVO();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(body);

		JsonNode resNode = rootNode.path("response");
		String email = resNode.get("email").asText();

		if (this.sns.isNaver()) {
				uservo.setNaver_email(email);
		} else if (this.sns.isGoogle()) {
				uservo.setGoogle_email(email);
		}
		return uservo;
	}
}
