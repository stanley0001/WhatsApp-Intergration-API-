package com.example.demo.WhatsApp.Services.Impl;
import com.example.demo.WhatsApp.Entities.Consumer;
import com.example.demo.WhatsApp.Entities.OutBox;
import com.example.demo.WhatsApp.Models.WhatsAppBody;
import com.example.demo.Clients.HttpClientBuilder;
import com.example.demo.WhatsApp.Repositories.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;

@Configuration
@Slf4j
@EnableAsync
public class Integrations {
    private final OutboxRepository outboxRepository;
    private final HttpClientBuilder http=new HttpClientBuilder();
    @Value("${url.faceBookBaseUrl}")
    private  String faceBookResourceUrl;
    private HttpHeaders headers;
    private String url;
    private String path;
    private Object callBack;

    public Integrations(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }
    //send whatsApp message
    @Async
     void sendMessage(Consumer consumer,OutBox messageToSend, WhatsAppBody message){
        log.info("Processing ....");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer "+consumer.getToken());
            try {
                ResponseEntity response = httpCall(headers, faceBookResourceUrl, null, consumer.getInstanceId(), "messages", HttpMethod.POST, message);
                JSONObject apiResponse = new JSONObject(String.valueOf(response.getBody()));
                String messageId = apiResponse.getJSONArray("messages").getJSONObject(0).getString("id");
                   if (messageId!=null){
                       messageToSend.setWhatsAppId(messageId);
                       messageToSend.setStatus("PROCESSED");
                       outboxRepository.save(messageToSend);
                   }
                   if (response.getStatusCode().equals(HttpStatus.OK)){
                       log.info(".Processed");
                   }
            } catch (Exception e) {
                log.error("Error {}", e.getMessage());
            }
    }
    //http method
    private ResponseEntity httpCall(HttpHeaders headers, String url, MultiValueMap requestParams, String pathUrl, String path, HttpMethod method, Object body){
        UriComponents uriComponents = http.getUriComponent(url,requestParams,pathUrl,path);
        return http.postEntity(uriComponents,body,headers,String.class, method);
    }
   //send callback to client
    @Async
    void callBack(HttpHeaders headers,String url,String path,Object callBack){
        this.headers = headers;
        this.url = url;
        this.path = path;
        this.callBack = callBack;
        try {
            httpCall(headers,url,null,null,path,HttpMethod.POST,callBack);
        }catch (Exception e){
            log.warn("Error processing callback {}",e.getMessage());
        }
   }
}