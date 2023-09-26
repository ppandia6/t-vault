
package com.tmobile.cso.vault.api.service;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tmobile.cso.vault.api.model.TMOMetaData;

@Component
public class AdminService extends MegService {

	@Value("${adminService.url:}")
	private  String adminServiceUrl;

	public String  getTMOAppMetadataDetails() throws Exception {
		String path = "/v1/platform-accounts";
		String url = adminServiceUrl + path + "?platformName=aws";
		Header[] headers = getAuthHeaders(path, "GET");
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeaders(headers);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		String resp = EntityUtils.toString(httpResponse.getEntity());
		return resp;
	}

}
