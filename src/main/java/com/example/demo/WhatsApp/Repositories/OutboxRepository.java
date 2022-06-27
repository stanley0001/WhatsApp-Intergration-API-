package com.example.demo.WhatsApp.Repositories;

import com.example.demo.WhatsApp.Entities.OutBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutboxRepository extends JpaRepository<OutBox,Long> {
    Optional<OutBox> findByWhatsAppId(String messageId);
}
