package com.example.demo.Nlp.Services;

import com.example.demo.Nlp.Models.BulkNlpRequest;
import com.example.demo.Nlp.Models.NlpRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface NlpService {
    Double wordMatch(NlpRequest match);
    HashMap<String,Double> bulkWordMatch(BulkNlpRequest match);
    String showBestMatch(BulkNlpRequest match);
}
