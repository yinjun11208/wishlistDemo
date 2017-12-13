package com.sample.wishlistDemo.api.wishlist;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sample.wishlistDemo.api.exceptions.CallingYaaSServiceException;

@Component
@PropertySource("classpath:default.properties")
public class OAuthWrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuthWrapper.class);

	@Value("${oauthURL}")
	private String URI;
	@Value("${yaaSClientsClient_ID}")
	private String clientId;
	@Value("${yaaSClientsClient_Secret}")
	private String clientSecret;
	@Value("${docuRepoScopes}")
	private String scopes;

	private String grantType = "client_credentials";

	public OAuthWrapper() {
	}

	public Optional<Map<String, Object>> getToken() {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("content-type", "application/x-www-form-urlencoded");
			RestTemplate restTemplate = new RestTemplate();
			String body = "grant_type=" + grantType + "&client_id=" + clientId + "&client_secret=" + clientSecret
					+ "&scope=" + scopes;
			HttpEntity<Object> request = new HttpEntity<>(body, headers);
			LOGGER.debug("process oauth request");
			Map<String, Object> tokenMap = restTemplate.postForObject(URI, request, Map.class);
			return Optional.of(tokenMap);
		} catch (RestClientException e) {
			throw new CallingYaaSServiceException();
		}
	}

}
