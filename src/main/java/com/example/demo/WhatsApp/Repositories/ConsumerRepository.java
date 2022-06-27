package com.example.demo.WhatsApp.Repositories;

import com.example.demo.WhatsApp.Entities.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsumerRepository extends JpaRepository<Consumer,Long> {
    Optional<Consumer> findByInstanceId(String instanceId);
}
