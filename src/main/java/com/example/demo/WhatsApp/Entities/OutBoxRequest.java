package com.example.demo.WhatsApp.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OutBoxRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true,nullable = false)
    private Long id;
    private String recipients;
    private Long consumerId;
    private String Message;
    private String status;
    private int retries;
    private Boolean scheduled;
    private LocalDateTime scheduleTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
