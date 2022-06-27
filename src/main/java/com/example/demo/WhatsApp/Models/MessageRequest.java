package com.example.demo.WhatsApp.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private List<String> recipients;
    private Long consumerId;
    private String Message;
    private Boolean scheduled;
    private LocalDateTime scheduleTime;
}
