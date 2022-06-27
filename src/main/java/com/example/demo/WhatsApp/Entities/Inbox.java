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
public class Inbox {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(unique = true,nullable = false)
        private Long id;
        private String messageFrom;
        private String messageTo;
        private String message;
        private String status;
        private String whatsAppId;
        private String messageType;
        private String time;
        private String instanceId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        @ManyToOne
        private Consumer consumer;


}
