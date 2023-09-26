package com.tmobile.cso.vault.api.service;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonNull;
import com.tmobile.cso.vault.api.controller.ControllerUtil;
import com.tmobile.cso.vault.api.model.TMOMetaData;
import com.tmobile.cso.vault.api.model.UserDetails;
import com.tmobile.cso.vault.api.process.RequestProcessor;
import com.tmobile.cso.vault.api.process.Response;
import com.tmobile.cso.vault.api.utils.JSONUtil;
import com.tmobile.cso.vault.api.utils.ThreadLocalContext;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@ComponentScan(basePackages = {"com.tmobile.cso.vault.api"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@PrepareForTest({ControllerUtil.class, JSONUtil.class, EntityUtils.class, HttpClientBuilder.class})
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*"})
public class WorkloadDetailsServiceTest {

    private MockMvc mockMvc;

    @InjectMocks
    WorkloadDetailsService workloadDetailsService;

    @Mock
    private RequestProcessor reqProcessor;

    @Mock
    UserDetails userDetails;

    @Mock
    VaultAuthService vaultAuthService;

    String token;

    @Mock
    CloseableHttpResponse httpResponse;

    @Mock
    HttpClientBuilder httpClientBuilder;

    @Mock
    StatusLine statusLine;

    @Mock
    HttpEntity mockHttpEntity;

    @Mock
    CloseableHttpClient httpClient1;

    @Before
    public void setUp() {
        mockStatic(ControllerUtil.class);
        mockStatic(JSONUtil.class);
        mockStatic(HttpClientBuilder.class);
        mockStatic(EntityUtils.class);
        ReflectionTestUtils.setField(workloadDetailsService, "workloadEndpoint", "testendpoint");
        ReflectionTestUtils.setField(workloadDetailsService, "workloadEndpointToken", "token12");

        Whitebox.setInternalState(ControllerUtil.class, "log", LogManager.getLogger(ControllerUtil.class));
        when(JSONUtil.getJSON(any(ImmutableMap.class))).thenReturn("log");

        Map<String, String> currentMap = new HashMap<>();
        currentMap.put("apiurl", "http://localhost:8080/vault/v2/ad");
        currentMap.put("user", "");
        ThreadLocalContext.setCurrentMap(currentMap);
        Whitebox.setInternalState(ControllerUtil.class, "log", LogManager.getLogger(ControllerUtil.class));
        when(JSONUtil.getJSON(any(ImmutableMap.class))).thenReturn("log");

        token = "5PDrOhsy4ig8L3EpsJZSLAMg";
        userDetails.setUsername("normaluser");
        userDetails.setAdmin(true);
        userDetails.setClientToken(token);
        userDetails.setSelfSupportToken(token);
        when(vaultAuthService.lookup(anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    }

    @Test
    public void test_getWorkloadDetails_success() throws Exception {
        String responseStr = "{\"items\": [{\"spec\": {\"id\": \"aac\",\"summary\": \"app1\"}}]}";
        String workloadResponse = "[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"},{\"appName\":\"app1\",\"appTag\":\"aac\",\"appID\":\"aac\"}]";

        ReflectionTestUtils.setField(workloadDetailsService, "workloadEndpoint", "http://appdetails.com");
        when(ControllerUtil.getCwmToken()).thenReturn("dG9rZW4=");
        when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLContext(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setRedirectStrategy(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.build()).thenReturn(httpClient1);
        when(httpClient1.execute(any())).thenReturn(httpResponse);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(mockHttpEntity);
        InputStream inputStream = new ByteArrayInputStream(responseStr.getBytes());
        when(mockHttpEntity.getContent()).thenReturn(inputStream);
        when(JSONUtil.getJSON(anyList())).thenReturn(workloadResponse);

        ResponseEntity<String> responseEntityExpected = ResponseEntity.status(HttpStatus.OK).body("[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"},{\"appName\":\"app1\",\"appTag\":\"aac\",\"appID\":\"aac\"}]");

        ResponseEntity<String> responseEntityActual = workloadDetailsService.getWorkloadDetails(token, userDetails);
        assertEquals(responseEntityExpected.getStatusCode(), responseEntityActual.getStatusCode());
        assertEquals(responseEntityExpected, responseEntityActual);

    }


    @Test(expected = Exception.class)
    public void test_getWorkloadDetails_success_Without_Apiresponse() throws Exception {
        String responseStr = "{\"items\": [{\"spec\": {\"id\": \"aac\",\"summary\": \"app1\"}}]}";
        String workloadResponse = "[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"},{\"appName\":\"app1\",\"appTag\":\"aac\",\"appID\":\"aac\"}]";

        ReflectionTestUtils.setField(workloadDetailsService, "workloadEndpoint", "http://appdetails.com");
        when(ControllerUtil.getCwmToken()).thenReturn("dG9rZW4=");
        when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLContext(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setRedirectStrategy(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.build()).thenReturn(httpClient1);
        when(httpClient1.execute(any())).thenReturn(httpResponse);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(null);
        InputStream inputStream = new ByteArrayInputStream(responseStr.getBytes());
        when(mockHttpEntity.getContent()).thenReturn(inputStream);
        when(JSONUtil.getJSON(anyList())).thenReturn(workloadResponse);

        ResponseEntity<String> responseEntityExpected = ResponseEntity.status(HttpStatus.OK).body("[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"},{\"appName\":\"app1\",\"appTag\":\"aac\",\"appID\":\"aac\"}]");
        workloadDetailsService.getWorkloadDetails(token, userDetails);
    }

    @Test
    public void test_getWorkloadDetails_success_WithoutDetails() throws Exception {
        String responseStr = "{\"items\": []}";
        String workloadResponse = "[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"},{\"appName\":\"app1\",\"appTag\":\"aac\",\"appID\":\"aac\"}]";

        ReflectionTestUtils.setField(workloadDetailsService, "workloadEndpointToken", null);
        when(ControllerUtil.getCwmToken()).thenReturn("dG9rZW4=");
        when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLContext(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setRedirectStrategy(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.build()).thenReturn(httpClient1);
        when(httpClient1.execute(any())).thenReturn(httpResponse);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(mockHttpEntity);
        InputStream inputStream = new ByteArrayInputStream(responseStr.getBytes());
        when(mockHttpEntity.getContent()).thenReturn(inputStream);
        when(JSONUtil.getJSON(anyList())).thenReturn(workloadResponse);

        ResponseEntity<String> responseEntityExpected = ResponseEntity.status(HttpStatus.OK).body("[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"},{\"appName\":\"app1\",\"appTag\":\"aac\",\"appID\":\"aac\"}]");

        ResponseEntity<String> responseEntityActual = workloadDetailsService.getWorkloadDetails(token, userDetails);
        assertEquals(responseEntityExpected.getStatusCode(), responseEntityActual.getStatusCode());
        assertEquals(responseEntityExpected, responseEntityActual);

    }

    @Test
    public void test_getWorkloadDetails_success_with_populate_null_details() throws Exception {
        String responseStr = "{\"items\": [{\"spec\": {\"id\": "+ JsonNull.INSTANCE+",\"summary\": "+ JsonNull.INSTANCE+"}}]}";
        String workloadResponse = "[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"},{\"appName\":\"app1\",\"appTag\":\"aac\",\"appID\":\"aac\"}]";

        ReflectionTestUtils.setField(workloadDetailsService, "workloadEndpoint", "http://appdetails.com");
        when(ControllerUtil.getCwmToken()).thenReturn("dG9rZW4=");
        when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLContext(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setRedirectStrategy(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.build()).thenReturn(httpClient1);
        when(httpClient1.execute(any())).thenReturn(httpResponse);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(mockHttpEntity);
        InputStream inputStream = new ByteArrayInputStream(responseStr.getBytes());
        when(mockHttpEntity.getContent()).thenReturn(inputStream);
        when(JSONUtil.getJSON(anyList())).thenReturn(workloadResponse);

        ResponseEntity<String> responseEntityExpected = ResponseEntity.status(HttpStatus.OK).body("[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"},{\"appName\":\"app1\",\"appTag\":\"aac\",\"appID\":\"aac\"}]");

        ResponseEntity<String> responseEntityActual = workloadDetailsService.getWorkloadDetails(token, userDetails);
        assertEquals(responseEntityExpected.getStatusCode(), responseEntityActual.getStatusCode());
        assertEquals(responseEntityExpected, responseEntityActual);

    }


    @Test
    public void test_getWorkloadDetails_failed() throws Exception {
        String workloadResponse = "[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"}]";

        ReflectionTestUtils.setField(workloadDetailsService, "workloadEndpoint", "http://appdetails.com");
        when(ControllerUtil.getCwmToken()).thenReturn("dG9rZW4=");
        when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLContext(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setRedirectStrategy(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.build()).thenReturn(httpClient1);
        when(httpClient1.execute(any())).thenReturn(httpResponse);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(400);

        when(JSONUtil.getJSON(anyList())).thenReturn(workloadResponse);

        ResponseEntity<String> responseEntityExpected = ResponseEntity.status(HttpStatus.OK).body("[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"}]");

        ResponseEntity<String> responseEntityActual = workloadDetailsService.getWorkloadDetails(token, userDetails);
        assertEquals(responseEntityExpected.getStatusCode(), responseEntityActual.getStatusCode());
        assertEquals(responseEntityExpected, responseEntityActual);

    }

    @Test
    public void test_getWorkloadDetails_failed_token() throws Exception {
        String workloadResponse = "[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"}]";

        ReflectionTestUtils.setField(workloadDetailsService, "workloadEndpoint", "http://appdetails.com");
        when(ControllerUtil.getCwmToken()).thenReturn("");
        when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLContext(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setRedirectStrategy(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.build()).thenReturn(httpClient1);
        when(httpClient1.execute(any())).thenReturn(httpResponse);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(400);

        when(JSONUtil.getJSON(anyList())).thenReturn(workloadResponse);

        ResponseEntity<String> responseEntityExpected = ResponseEntity.status(HttpStatus.OK).body("[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"}]");

        ResponseEntity<String> responseEntityActual = workloadDetailsService.getWorkloadDetails(token, userDetails);
        assertEquals(responseEntityExpected.getStatusCode(), responseEntityActual.getStatusCode());
        assertEquals(responseEntityExpected, responseEntityActual);

    }
    
    
    @Test
    public void test_getWorkloadDetailsByAppName_success() throws Exception {
        String appName = "appName";

        String responseStr = "{\"items\": [{\"spec\": {\"id\": \"aac\",\"summary\": \"app1\"}}]}";
        String workloadResponse = "[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"},{\"appName\":\"app1\",\"appTag\":\"aac\",\"appID\":\"aac\"}]";

        ReflectionTestUtils.setField(workloadDetailsService, "workloadEndpoint", "http://appdetails.com");
        when(ControllerUtil.getCwmToken()).thenReturn("dG9rZW4=");
        when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLContext(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setRedirectStrategy(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.build()).thenReturn(httpClient1);
        when(httpClient1.execute(any())).thenReturn(httpResponse);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(mockHttpEntity);
        InputStream inputStream = new ByteArrayInputStream(responseStr.getBytes());
        when(mockHttpEntity.getContent()).thenReturn(inputStream);
        when(JSONUtil.getJSON(anyList())).thenReturn(workloadResponse);

        ResponseEntity<String> responseEntityExpected = ResponseEntity.status(HttpStatus.OK).body("[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"},{\"appName\":\"app1\",\"appTag\":\"aac\",\"appID\":\"aac\"}]");

        ResponseEntity<String> responseEntityActual = workloadDetailsService.getWorkloadDetailsByAppName(appName);
        assertEquals(responseEntityExpected.getStatusCode(), responseEntityActual.getStatusCode());
    }
    
    @Test
    public void test_getWorkloadDetailsByAppName_fail() throws Exception {
        String appName = "appName";

        String responseStr = "{\"items\": [{\"spec\": {\"id\": \"aac\",\"summary\": \"app1\"}}]}";
        String workloadResponse = null;

        ReflectionTestUtils.setField(workloadDetailsService, "workloadEndpoint", "http://appdetails.com");
        when(ControllerUtil.getCwmToken()).thenReturn("dG9rZW4=");
        when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLContext(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setRedirectStrategy(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.build()).thenReturn(httpClient1);
        when(httpClient1.execute(any())).thenReturn(httpResponse);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(500);
        when(httpResponse.getEntity()).thenReturn(mockHttpEntity);
        InputStream inputStream = new ByteArrayInputStream(responseStr.getBytes());
        when(mockHttpEntity.getContent()).thenReturn(inputStream);
        when(JSONUtil.getJSON(anyList())).thenReturn(workloadResponse);

        ResponseEntity<String> responseEntityExpected = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\"},{\"appName\":\"app1\",\"appTag\":\"aac\",\"appID\":\"aac\"}]");

        ResponseEntity<String> responseEntityActual = workloadDetailsService.getWorkloadDetailsByAppName(appName);
        assertEquals(responseEntityExpected.getStatusCode(), responseEntityActual.getStatusCode());
    }

    @Test
    public void test_getAllApplicationDetailsFromCLM_success() throws Exception {

        String responseStr = "{\"items\": [{\"spec\": {\"id\": \"Other\",\"tag\": \"Other\", \"summary\": \"app1\", \"brtContactEmail\": \"owner@company.com\", \"projectLeadEmail\": \"lead@company.com\"}}]}";
        String workloadResponse = "[{\"appName\":\"Other\",\"appTag\":\"Other\",\"appID\":\"oth\", \"applicationOwnerEmailId\":\"owner@company.com\", \"projectLeadEmailId\":\"lead@company.com\"}]";

        ReflectionTestUtils.setField(workloadDetailsService, "workloadEndpoint", "http://appdetails.com");
        when(ControllerUtil.getCwmToken()).thenReturn("dG9rZW4=");
        when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setSSLContext(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.setRedirectStrategy(any())).thenReturn(httpClientBuilder);
        when(httpClientBuilder.build()).thenReturn(httpClient1);
        when(httpClient1.execute(any())).thenReturn(httpResponse);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(mockHttpEntity);
        InputStream inputStream = new ByteArrayInputStream(responseStr.getBytes());
        when(mockHttpEntity.getContent()).thenReturn(inputStream);
        when(JSONUtil.getJSON(anyList())).thenReturn(workloadResponse);

        TMOMetaData expectedTmoAppMetadataDetails = new TMOMetaData();
        expectedTmoAppMetadataDetails.setApplicationName("Other");
        expectedTmoAppMetadataDetails.setApplicationOwnerEmailId("owner@company.com");
        expectedTmoAppMetadataDetails.setProjectLeadEmailId("lead@company.com");
        expectedTmoAppMetadataDetails.setApplicationTag("Other");
        List<TMOMetaData> tmoAppMetadataDetailsList = workloadDetailsService.getAllApplicationDetailsFromCLM();
        assertEquals(expectedTmoAppMetadataDetails.getApplicationName(), tmoAppMetadataDetailsList.get(0).getApplicationName());
    }

    @Test
    public void test_getAllAppMetadata_success() throws Exception {

        Response listResponse = new Response();
        listResponse.setHttpstatus(HttpStatus.OK);
        listResponse.setResponse("{ \"data\": { \"keys\": [ \"other\"] } }");
        String pathStrForList = "metadata/tmo-applications?list=true";
        when(reqProcessor.process("/tmo-applicaions", "{\"path\":\""+pathStrForList+"\"}",token)).thenReturn(listResponse);
        Response readResponse = new Response();
        readResponse.setHttpstatus(HttpStatus.OK);
        readResponse.setResponse("{\"data\": {\"applicationName\": \"other\", \"applicationOwnerEmailId\": \"owner@company.com\", \"applicationTag\": \"other\", \"externalCertificateList\": [], \"internalCertificateList\": [\"cert1.company.com\"], \"projectLeadEmailId\": \"lead@company.com\",  \"updateFlag\": true }}");
        String pathStr = "metadata/tmo-applications/other";
        when(reqProcessor.process("/tmo-applicaions","{\"path\":\""+pathStr+"\"}",token)).thenReturn(readResponse);
        List<String> certList = new ArrayList<>();
        certList.add("cert1.company.com");
        TMOMetaData tmoAppMetadataDetails = new TMOMetaData("other", "owner@company.com", "other", "lead@company.com", "other", certList, new ArrayList<>(),"other","other","other","other","other");
        List<TMOMetaData> tmoAppMetadataDetailsList = new ArrayList<>();
        tmoAppMetadataDetailsList.add(tmoAppMetadataDetails);
        List<TMOMetaData> appMetadataList = workloadDetailsService.getAllAppMetadata(token);
        assertEquals(tmoAppMetadataDetailsList, appMetadataList);
    }

    @Test
    public void test_getAllAppMetadata_failed_toReadAppMetadata() throws Exception {

        Response listResponse = new Response();
        listResponse.setHttpstatus(HttpStatus.OK);
        listResponse.setResponse("{ \"data\": { \"keys\": [ \"other\"] } }");
        String pathStrForList = "metadata/tmo-applications?list=true";
        when(reqProcessor.process("/tmo-applicaions", "{\"path\":\""+pathStrForList+"\"}",token)).thenReturn(listResponse);
        Response readResponse = new Response();
        readResponse.setHttpstatus(HttpStatus.NO_CONTENT);
        String pathStr = "metadata/tmo-applications/other";
        when(reqProcessor.process("/tmo-applicaions","{\"path\":\""+pathStr+"\"}",token)).thenReturn(readResponse);
        List<String> certList = new ArrayList<>();
        certList.add("cert1.company.com");
        List<TMOMetaData> tmoAppMetadataDetailsList = new ArrayList<>();
        List<TMOMetaData> appMetadataList = workloadDetailsService.getAllAppMetadata(token);
        assertEquals(tmoAppMetadataDetailsList, appMetadataList);
    }

    @Test
    public void test_getAllAppMetadata_failed_toParseMetadat() throws Exception {

        Response listResponse = new Response();
        listResponse.setHttpstatus(HttpStatus.OK);
        listResponse.setResponse("{ \"data\": { \"keys\": [ \"other\"] } }");
        String pathStrForList = "metadata/tmo-applications?list=true";
        when(reqProcessor.process("/tmo-applicaions", "{\"path\":\""+pathStrForList+"\"}",token)).thenReturn(listResponse);
        Response readResponse = new Response();
        readResponse.setHttpstatus(HttpStatus.OK);
        readResponse.setResponse("{\"data1\": {\"applicationName\": \"other\", \"applicationOwnerEmailId\": \"owner@company.com\", \"applicationTag\": \"other\", \"externalCertificateList\": [], \"internalCertificateList\": [\"cert1.company.com\"], \"projectLeadEmailId\": \"lead@company.com\",  \"updateFlag\": true }}");
        String pathStr = "metadata/tmo-applications/other";
        when(reqProcessor.process("/tmo-applicaions","{\"path\":\""+pathStr+"\"}",token)).thenReturn(readResponse);
        List<String> certList = new ArrayList<>();
        certList.add("cert1.company.com");
        List<TMOMetaData> tmoAppMetadataDetailsList = new ArrayList<>();
        List<TMOMetaData> appMetadataList = workloadDetailsService.getAllAppMetadata(token);
        assertEquals(tmoAppMetadataDetailsList, appMetadataList);
    }

    @Test
    public void test_udpateApplicationMetadata_success() throws Exception {
        List<String> certList = new ArrayList<>();
        certList.add("cert1.company.com");
        TMOMetaData tmoAppMetadataDetails = new TMOMetaData("other", "owner@company.com", "other", "lead@company.com", "other", certList, new ArrayList<>(),"other","other","other","other","other");
        TMOMetaData tmoAppMetadataDetailsCLM = new TMOMetaData("other", "owner@company.com", "other", "lead@company.com", "other", certList, new ArrayList<>(),"other","other","other","other","other");

        Response expectedResponse = new Response();
        expectedResponse.setHttpstatus(HttpStatus.NO_CONTENT);
        when(reqProcessor.process(eq("/write"), Mockito.any(), eq(token))).thenReturn(expectedResponse);
        Response actualResponse = workloadDetailsService.udpateApplicationMetadata(token, tmoAppMetadataDetails, tmoAppMetadataDetailsCLM);
        assertEquals(expectedResponse.getHttpstatus(), actualResponse.getHttpstatus());
    }
    @Test
    public void test_udpateApplicationMetadata_failure() throws Exception {
        List<String> certList = new ArrayList<>();
        certList.add("cert1.company.com");
        TMOMetaData tmoAppMetadataDetails = new TMOMetaData("other", "owner@company.com", "other", "lead@company.com", "other", certList, new ArrayList<>(),"other","other","other","other","other");
        TMOMetaData tmoAppMetadataDetailsCLM = new TMOMetaData("other", "owner@company.com", "other", "lead@company.com", "other", certList, new ArrayList<>(),"other","other","other","other","other");

        Response expectedResponse = new Response();
        expectedResponse.setHttpstatus(HttpStatus.NO_CONTENT);
        when(reqProcessor.process(eq("/write"), Mockito.any(), eq(token))).thenReturn(expectedResponse);
        Response actualResponse = workloadDetailsService.udpateApplicationMetadata(token, tmoAppMetadataDetails, tmoAppMetadataDetailsCLM);
        assertEquals(expectedResponse.getHttpstatus(), actualResponse.getHttpstatus());
    }
}
