package com.example.demo.Clients;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@Slf4j
public class HttpClientBuilder<T extends Object> {

    private Integer timeout = 30000;

    private AppResponseModel responseModel;

    public ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        clientHttpRequestFactory.setReadTimeout(timeout);
        return clientHttpRequestFactory;
    }

    public ResponseEntity postEntity(UriComponents components, T token, HttpHeaders userHeaders, Class<?> responseType, HttpMethod method) {
        try {
            URI urlb = components.toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            if (userHeaders != null && !userHeaders.isEmpty()) {
                headers.addAll(userHeaders);
            }
            HttpEntity<T> entity = new HttpEntity<>(token, headers);
            RestTemplate template = new RestTemplate(getClientHttpRequestFactory());
            template.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
            return template.exchange(urlb, method, entity, responseType);
        } catch (HttpClientErrorException ex) {
            log.error("HttpClientErrorException=[statusCode={} responseBody={}]", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            return errorResponseBuilder(ex.getStatusCode(), ex);
        } catch (Exception ex) {
            log.error("Error {}", ex.getMessage());
            return errorResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
    }

    public ResponseEntity getEntity(UriComponents components, HttpHeaders userHeaders, Class<?> responseType) {
        try {
            URI finalURL = components.toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            if (userHeaders != null && !userHeaders.isEmpty()) {
                headers.addAll(userHeaders);
            }
            HttpEntity<Object> entity = new HttpEntity<>(HttpEntity.EMPTY, headers);
            RestTemplate template = new RestTemplate(getClientHttpRequestFactory());
            template.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
            ResponseEntity<?> responseEntity = template.exchange(finalURL, HttpMethod.GET, entity, responseType);
            return responseEntity;
        } catch (HttpClientErrorException ex) {
            log.error("HttpClientErrorException=[statusCode={} responseBody={}]", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            return errorResponseBuilder(ex.getStatusCode(), ex);
        } catch (Exception ex) {
            log.error("error=[{}]", ex.getMessage());
            return errorResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
    }
    private ResponseEntity errorResponseBuilder(HttpStatus httpStatus, Exception ex) {

        String message = "";
        if (ex instanceof HttpClientErrorException) {
            try {
                JSONObject jsonObject = new JSONObject(((HttpClientErrorException) ex).getResponseBodyAsString());
                if (jsonObject.has("error")) {
                    message = message + jsonObject.getString("error") + ". ";
                }
                if (jsonObject.has("message")) {
                    System.out.println(message);
                    message = message + jsonObject.getString("message") + ". ";
                }
            } catch (Exception e) {
                log.error("error={}", e.getMessage());
                message = ((HttpClientErrorException) ex).getResponseBodyAsString();
            }
        } else {
            message = ex.getMessage();
        }

        return ResponseEntity.badRequest().body(message);
    }

    public UriComponents getUriComponent(String resourceURL, MultiValueMap<String, String> requestParams, String... pathUrl) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(resourceURL).pathSegment(pathUrl);
        return requestParams == null || requestParams.isEmpty()
                ? builder.build()
                : builder.queryParams(requestParams).build();
    }

}
