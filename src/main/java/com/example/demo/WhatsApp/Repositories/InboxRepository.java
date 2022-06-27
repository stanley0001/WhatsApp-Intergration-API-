package com.example.demo.WhatsApp.Repositories;

import com.example.demo.WhatsApp.Entities.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InboxRepository extends JpaRepository<Inbox,Long> {
    Optional<Inbox> findByWhatsAppId(String messageId);
}
