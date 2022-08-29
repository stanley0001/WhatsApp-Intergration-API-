package com.example.demo.Nlp.Impl;

import com.example.demo.Nlp.Models.BulkNlpRequest;
import com.example.demo.Nlp.Models.NlpRequest;
import com.example.demo.Nlp.Services.NlpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class NlpImplementation implements NlpService {
    public Double wordMatch(NlpRequest request){
        double response=0.0;
        //word matching algorithm
          try {
              response=StringUtils.getJaroWinklerDistance(request.getWord().toUpperCase(), request.getMatch().toUpperCase())*100;
          }catch (Exception e){
              log.error("error matching words: {}",e.getMessage());
          }
        return response;
    }
    public HashMap<String,Double> bulkWordMatch(BulkNlpRequest request){
        HashMap<String,Double> response=new HashMap<>();
        //word matching algorithm
        for (String match1:
                request.getMatch()) {
            NlpRequest nlpRequest=new NlpRequest();
            nlpRequest.setWord(request.getWord());
            nlpRequest.setMatch(match1);
            Double currentMatching=this.wordMatch(nlpRequest);
            response.put(match1,currentMatching);
        }
        return response;
    }
    public String showBestMatch(BulkNlpRequest request){
        String response= null;
        HashMap<String,Double> bestMatch=this.bulkWordMatch(request);
        Double highestRank=0.0;
        for (Map.Entry<String, Double> entry:
             bestMatch.entrySet()) {
            if (entry.getValue()>highestRank)
                response=entry.getKey();
        }
        return response;
    }
}
