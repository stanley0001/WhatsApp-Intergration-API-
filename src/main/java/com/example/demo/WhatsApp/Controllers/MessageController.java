package com.example.demo.WhatsApp.Controllers;

import com.example.demo.WhatsApp.Entities.Consumer;
import com.example.demo.WhatsApp.Models.BulkWhatsappMessaging;
import com.example.demo.WhatsApp.Models.MessageRequest;
import com.example.demo.WhatsApp.Services.CommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("communication")
@Slf4j
public class MessageController {
    public final CommunicationService communicationService;

    public MessageController(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @PostMapping("/whatsappIncoming")
    public ResponseEntity<?> externalModelSync(@RequestBody String req) throws JSONException {
        communicationService.processWhatsappInbox(req);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/whatsappIncoming")
    public ResponseEntity<?> whatsappGet(@RequestParam(name = "hub.challenge")  String hubChallenge,@RequestParam(name = "hub.mode") String hubMode,  @RequestParam(name = "hub.verify_token")  String hubAccessToken) {

        log.info("get params {},{},{}",hubMode, hubChallenge,hubAccessToken);
        //verify token

        return new ResponseEntity<>(hubChallenge,HttpStatus.OK);
    }
    @PostMapping("/createConsumer")
    public ResponseEntity<?> createConsumer(@RequestBody Consumer req) {
        Consumer consumer=communicationService.createConsumer(req);
        return new ResponseEntity<>(consumer,HttpStatus.OK);
    }
    @PostMapping("/getAllConsumers")
    public ResponseEntity<List<Consumer>> getAllConsumers() {
        List<Consumer> consumers=communicationService.getAllConsumers();
        return new ResponseEntity<>(consumers,HttpStatus.OK);
    }

    @PostMapping("/sendCustomWhatsappMessage")
    public ResponseEntity<?> sendCustomWhatsappMessage(@RequestBody BulkWhatsappMessaging message) {
        communicationService.sendCustomWhatsappMessage(message);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
