package com.example.demo.WhatsApp.Services.Impl;
import com.example.demo.WhatsApp.Models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.util.*;
import com.example.demo.WhatsApp.Entities.Consumer;
import com.example.demo.WhatsApp.Entities.Inbox;
import com.example.demo.WhatsApp.Entities.OutBox;
import com.example.demo.WhatsApp.Entities.OutBoxRequest;
import com.example.demo.WhatsApp.Repositories.ConsumerRepository;
import com.example.demo.WhatsApp.Repositories.InboxRepository;
import com.example.demo.WhatsApp.Repositories.OutBoxRequestRepo;
import com.example.demo.WhatsApp.Repositories.OutboxRepository;
import com.example.demo.WhatsApp.Services.CommunicationService;

@Service
@Slf4j
@EnableAsync
@EnableScheduling
public class WhatsAppEntry implements CommunicationService {
    private final InboxRepository inboxRepo;
    private final Integrations integration;
    private final OutboxRepository outboxRepo;
    private final OutBoxRequestRepo outBoxRequestRepo;
    private final ConsumerRepository consumerRepo;
    @Value("${config.numberOfRetries}")
    private  int messageRetries;

    public WhatsAppEntry(InboxRepository inboxRepo, Integrations integration, OutboxRepository outboxRepo, OutBoxRequestRepo outBoxRequestRepo, ConsumerRepository consumerRepo) {
        this.inboxRepo = inboxRepo;
        this.integration = integration;
        this.outboxRepo = outboxRepo;
        this.outBoxRequestRepo = outBoxRequestRepo;
        this.consumerRepo = consumerRepo;
    }
    //getting all consumers
    public List<Consumer> getAllConsumers(){
        return consumerRepo.findAll();
    }
    //creating a consumer
    public Consumer createConsumer(Consumer consumer){
        try {
            return consumerRepo.save(consumer);
        }catch (Exception e){
            log.warn("Error creating a consumer {}",e.getMessage());
        }
        return null;
    }
    //processing a whatsapp message
    @Async
    public void processWhatsappInbox(String request) {
        try {
            JSONObject received = new JSONObject(request);
            if (received.has("entry")){
                this.processWhatsappInbox(received);
            }
        }catch (Exception e){
            log.warn("Invalid body received, Error(s) {}",e.getMessage());
        }
    }
    @Async
    void processWhatsappInbox(JSONObject request) {
        try {
            JSONObject receivedBody=request.getJSONArray("entry").getJSONObject(0).getJSONArray("changes").getJSONObject(0).getJSONObject("value");
            JSONObject inBoxObject;
             JSONArray messages=receivedBody.getJSONArray("messages");
             inBoxObject=messages.getJSONObject(0);
             try {
                 JSONArray contact=receivedBody.getJSONArray("contacts");
                 String to=contact.getJSONObject(0).getString("wa_id");
                 String from=inBoxObject.getString("from");
                 JSONObject messageObject=inBoxObject.getJSONObject("text");
                 String message=messageObject.getString("body");
                 String time=inBoxObject.getString("timestamp");
                 String instanceId=receivedBody.getJSONObject("metadata").getString("phone_number_id");
                 Inbox whatsAppMessage=new Inbox();
                 String senderName=contact.getJSONObject(0).getJSONObject("profile").getString("name");
                 whatsAppMessage.setMessage(String.valueOf(message));
                 whatsAppMessage.setMessageType("WHATSAPP");
                 whatsAppMessage.setMessageFrom(from);
                 whatsAppMessage.setMessageTo(to);
                 whatsAppMessage.setTime(time);
                 whatsAppMessage.setInstanceId(instanceId);
                 Inbox receivedMessage=this.saveWhatsAppInbox(whatsAppMessage);
                 String responseMessage=this.processResponse(senderName);
                 if (responseMessage!=null){
                     Optional<Consumer> consumer=consumerRepo.findByInstanceId(instanceId);
                     if (consumer.isPresent()){
                         try {
                             this.callBack(consumer.get().getCallBackUrl(),receivedMessage);
                         }catch (Exception e){
                             log.warn("error while processing callback {}",e.getMessage());
                         }
                     }
                    this.sendFaceBookMessage(instanceId,to,responseMessage);
                 }
             }catch (Exception e){
                 log.warn("error getting message content {}",e.getMessage());
             }
        }catch (Exception e){
             //process messageStatus
            this.processMessageStatus(request);
        }
    }
    //process messageStatus
    @Async
    void processMessageStatus(JSONObject request) {
        try {
            JSONObject receivedBody=request.getJSONArray("entry").getJSONObject(0).getJSONArray("changes").getJSONObject(0).getJSONObject("value");
            JSONArray statuses=receivedBody.getJSONArray("statuses");
            JSONObject messageIdObject=statuses.getJSONObject(0);
            String messageId=messageIdObject.getString("id");
            String status=messageIdObject.getString("status").toUpperCase();
            Optional<OutBox> message=outboxRepo.findByWhatsAppId(messageId);
            if (message.isPresent()){
                this.updateMessageStatus(message.get(),status);
            }else {
                log.warn("no outbox message found");
            }
            if (status.equals("FAILED")){
                try {
                    JSONArray error=messageIdObject.getJSONArray("errors");
                    JSONObject actualError=error.getJSONObject(0);
                    String errorCode=actualError.getString("code");
                    String errorDescription=actualError.getString("title");
                    log.error("Failed to send message with code {} ad error {}",errorCode,errorDescription);
                }catch (Exception e){
                    log.warn("could no get error message {}",e.getMessage());
                }
            }
        }catch (Exception e){
            log.warn("Error(s) retrieving message status {}",e.getMessage());
        }
    }
    private String processResponse(String sender){
        log.info("received message from sender {}",sender);
            //send response
        try {
          return "Hi "+sender ;
        }catch (Exception e){
         log.warn(e.getMessage());
        }
            return null;
    }
    public Inbox saveWhatsAppInbox(Inbox message){
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        try {
            return inboxRepo.save(message);
        }catch (Exception e){
            log.error("Error saving message {}",e.getMessage());
        }
        return null;
    }
    public OutBox saveWhatsAppOutbox(OutBox message){
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        try {
            return outboxRepo.save(message);
        }catch (Exception e){
            log.error("Error saving message {}",e.getMessage());
        }
        return null;
    }
    @Async
   void updateMessageStatus(OutBox message,String status){
        try {
            log.info("updating message id {} status {}",message.getId(),status);
            Optional<OutBox> inbox=outboxRepo.findById(message.getId());
            if (inbox.isPresent()){
                message=inbox.get();
                message.setStatus(status);
                this.saveWhatsAppOutbox(message);
                //callback to consumer
                try {
                    Optional<Consumer> consumer=consumerRepo.findByInstanceId(inbox.get().getInstanceId());
                    if (consumer.isPresent()){
                        StatusMessage statusMessage=new StatusMessage();
                        statusMessage.setStatus(status);
                        statusMessage.setMessage(message);
                        this.callBack(consumer.get().getCallBackUrl(),statusMessage);
                    }
                }catch (Exception e){
                    log.warn("error while processing callback {}",e.getMessage());
                }
            }else {
                log.warn("message with id {} not found",message.getId());
            }
        }catch (Exception e){
            log.warn("Unable to update message status {}",e.getMessage());
        }
   }
    private OutBox createMessageBody(String to, String message, Consumer consumer) {
        OutBox sentMessage=new OutBox();
        sentMessage.setMessage(message);
        sentMessage.setMessageTo(to);
        sentMessage.setStatus("NEW");
        sentMessage.setMessageFrom(consumer.getName());
        sentMessage.setMessageType("WHATSAPP");
        sentMessage.setInstanceId(consumer.getInstanceId());
        sentMessage.setConsumer(consumer);
        return this.saveWhatsAppOutbox(sentMessage);
    }
  //creating whatsApp body
    public WhatsAppBody createWhatsAppBody(String to,String message){
        WhatsAppText text=new WhatsAppText();
        text.setBody(message);
        WhatsAppBody messageBody=new WhatsAppBody();
        messageBody.setMessaging_product("whatsapp");
        messageBody.setPreview_url(false);
        messageBody.setRecipient_type("individual");
        messageBody.setTo(to);
        messageBody.setText(text);
        messageBody.setType("text");
        return messageBody;
    }
    //send whatsApp message using facebookAPI
    @Async
     void sendFaceBookMessage(String instanceId,String to,String message){
        //get consumer
        Optional<Consumer> consumer=consumerRepo.findByInstanceId(instanceId);
        if (consumer.isPresent()) {
            //create outbox entry
            OutBox messageToSend =this.createMessageBody(to,message,consumer.get());
           //create whatsApp body
         WhatsAppBody body=this.createWhatsAppBody(to,message);
         //send message
            try {
                integration.sendMessage(consumer.get(),messageToSend,body);
            }catch (Exception e){
                log.info("Error(s) {}",e.getMessage());
            }
        }else {
            log.warn("Consumer with id {} not present",instanceId);
        }
    }
    //express callback
    public  void fowardCallback(String req){
        log.info("processing daraja response: {}",req);
        this.callBack("http://localhost:30003/mpesa/callbackListener",req);
    }
    //call back implementation
    @Async
    public void callBack(String url,Object callBAckData){
        HttpHeaders headers= new HttpHeaders();
        headers.add("Content-Type","application/json");
        headers.add("Accept","*/*");
        integration.callBack(headers,url,null,callBAckData);
    }
    //custom whatsapp message
     @Async
     public void sendCustomWhatsappMessage(BulkWhatsappMessaging messageRequest) {
         for (int i = 0; i <= messageRequest.getMessageRequestList().size(); i++){
             MessageRequest message=messageRequest.getMessageRequestList().get(i);
             for (String recipient :
                     message.getRecipients()) {
                 OutBoxRequest outBoxRequest = new OutBoxRequest();
                 outBoxRequest.setRecipients(recipient);
                 outBoxRequest.setMessage(message.getMessage());
                 outBoxRequest.setStatus("NEW");
                 outBoxRequest.setRetries(0);
                 outBoxRequest.setConsumerId(message.getConsumerId());
                 outBoxRequest.setScheduled(message.getScheduled());
                 outBoxRequest.setScheduleTime(message.getScheduleTime());
                 outBoxRequest.setCreatedAt(LocalDateTime.now());
                 outBoxRequest.setUpdatedAt(LocalDateTime.now());
                 outBoxRequest.setScheduleTime(message.getScheduleTime());
                 try {
                     outBoxRequestRepo.save(outBoxRequest);
                 } catch (Exception e) {
                     log.warn("Error saving outbox request");
                 }
             }
     }
    }
    @Scheduled(fixedRate = 500)
    @Async
    public void sendWhatsappMessage(){
        Optional<List<OutBoxRequest>> outBoxRequests=outBoxRequestRepo.findAllByStatus("NEW");
        if (outBoxRequests.isPresent()){
            for (OutBoxRequest messageRequest:
            outBoxRequests.get()) {
                //get Consumer
                Optional<Consumer> consumer = consumerRepo.findById(messageRequest.getConsumerId());
                if (consumer.isPresent()){
                    //to replaced by a variable
                    if (messageRequest.getRetries() < messageRetries) {
                        if (messageRequest.getScheduled() == Boolean.FALSE || messageRequest.getScheduled()==null) {
                            //process message
                            ProcessingStatus(messageRequest, consumer.get());
                        } else {
                            //check schedule time
                            if (messageRequest.getScheduleTime().isBefore(LocalDateTime.now())) {
                                //process message
                                ProcessingStatus(messageRequest, consumer.get());
                            }
                        }
                    }
                }
            }
        }
    }
    private void ProcessingStatus(OutBoxRequest messageRequest, Consumer consumer) {
        sendFaceBookMessage(consumer.getInstanceId(), messageRequest.getRecipients(), messageRequest.getMessage());
        try {
            messageRequest.setStatus("PROCESSED");
            messageRequest.setRetries(messageRequest.getRetries()+1);
            outBoxRequestRepo.save(messageRequest);
        }catch (Exception e){
            log.warn(e.getMessage());
        }
    }
    @Scheduled(fixedRate = 100)
    @Async
    public void deleteProcessedRequests(){
        Optional<List<OutBoxRequest>> processedRequests=outBoxRequestRepo.findAllByStatus("PROCESSED");
         if (processedRequests.isPresent()){
             for (OutBoxRequest req:
                 processedRequests.get()) {
                 if (req.getScheduleTime()==null || req.getScheduleTime().isBefore(LocalDateTime.now().minusSeconds(10))){
                     try {
                         outBoxRequestRepo.delete(req);
                     }catch (Exception e){
                         log.warn(e.getMessage());
                     }
                 }
             }
         }
    }
}