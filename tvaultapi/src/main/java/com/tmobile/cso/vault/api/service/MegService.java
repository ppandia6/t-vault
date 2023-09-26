package com.tmobile.cso.vault.api.service;

import java.util.LinkedHashMap;
import java.util.Base64;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.Header;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;

import com.tmobile.security.taap.poptoken.builder.PopTokenBuilder;
import com.tmobile.security.taap.poptoken.builder.PopEhtsKey;

public class MegService {

	@Value("${meg.authHost:}")
	private String authHost;
	@Value("${meg.authPath:}")
	private String authPath;
	@Value("${meg.privateKey:}")
	private String privateKey;
	@Value("${meg.clientId:}")
	private String clientId;
	@Value("${meg.clientSecret:}")
	private String clientSecret;

	public Header[] getAuthHeaders(String path, String httpMethod) throws Exception {
		// generate pop token for token request
		String popToken = getPopToken("Basic " + getBasicAuth(), authPath, "POST");
		// call the token api
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(authHost + authPath);
		httpPost.addHeader("Authorization", "Basic " + getBasicAuth());
		httpPost.addHeader("X-Authorization", popToken);
		HttpResponse httpResponse = httpClient.execute(httpPost);
		String resp = EntityUtils.toString(httpResponse.getEntity());
		if (httpResponse.getStatusLine().getStatusCode() >= 300) {
			throw new Exception("meg auth failed: " + resp);
		}
		JSONObject respObj = new JSONObject(resp);
		// generate the pop token for the target API request
		String authHeader = "Bearer " + respObj.getString("access_token");
		// String nextPopToken = getPopToken(authHeader, path, httpMethod);
		// return bearer token and 2nd pop token for auth to the target API
		Header[] headers = { new BasicHeader("Authorization", authHeader),
				// new BasicHeader("X-Authorization", nextPopToken)
		};
		return headers;
	}

	private String getBasicAuth() {
		String combined = clientId + ":" + clientSecret;
		return Base64.getEncoder().encodeToString(combined.getBytes());
	}

	public String getPopToken(String authHeader, String path, String httpMethod) throws Exception {
		LinkedHashMap<String, String> ehtsKeyValueMap = new LinkedHashMap<>();
		ehtsKeyValueMap.put("Authorization", authHeader);
		ehtsKeyValueMap.put(PopEhtsKey.URI.keyName(), path);
		ehtsKeyValueMap.put(PopEhtsKey.HTTP_METHOD.keyName(), httpMethod);
		String popToken = PopTokenBuilder.newInstance().setEhtsKeyValueMap(ehtsKeyValueMap).signWith(privateKey)
				.build();

		return popToken;
	}
}
