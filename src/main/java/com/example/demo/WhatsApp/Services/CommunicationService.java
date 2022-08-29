package com.example.demo.WhatsApp.Services;

import com.example.demo.WhatsApp.Entities.Consumer;
import com.example.demo.WhatsApp.Models.BulkWhatsappMessaging;
import com.example.demo.WhatsApp.Models.MessageRequest;
import org.json.JSONException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
public interface CommunicationService {
    void processWhatsappInbox(String req) throws JSONException;
    Consumer createConsumer(Consumer req);
    List<Consumer> getAllConsumers();
    void sendCustomWhatsappMessage(BulkWhatsappMessaging message);
    @Scheduled(fixedRate = 5000)
    void sendWhatsappMessage();
    @Scheduled(fixedRate = 1000)
    void deleteProcessedRequests();

    void fowardCallback(String req);
}
