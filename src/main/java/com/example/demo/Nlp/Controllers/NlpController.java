package com.example.demo.Nlp.Controllers;

import com.example.demo.Nlp.Models.BulkNlpRequest;
import com.example.demo.Nlp.Models.NlpRequest;
import com.example.demo.Nlp.Services.NlpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("nlp")
public class NlpController {
    private final NlpService nlpService;

    public NlpController(NlpService nlpService) {
        this.nlpService = nlpService;
    }
    @PostMapping("wordMatch")
    ResponseEntity wordMatch(@RequestBody NlpRequest request){
        Double match=nlpService.wordMatch(request);
        return new ResponseEntity(match, HttpStatus.OK);
    }
    @PostMapping("bulkWordMatch")
    ResponseEntity bulkWordMatch(@RequestBody BulkNlpRequest request){
        HashMap<String,Double> match=nlpService.bulkWordMatch(request);
        return new ResponseEntity(match, HttpStatus.OK);
    }
    @PostMapping("bestMatch")
    ResponseEntity bestMatch(@RequestBody BulkNlpRequest request){
        String match=nlpService.showBestMatch(request);
        return new ResponseEntity(match, HttpStatus.OK);
    }

}
