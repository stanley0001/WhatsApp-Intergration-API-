package com.example.demo.Nlp.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkNlpRequest {
    private String word;
    private Collection<String> match;
}
