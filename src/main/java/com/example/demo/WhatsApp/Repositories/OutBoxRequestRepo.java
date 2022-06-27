package com.example.demo.WhatsApp.Repositories;

import com.example.demo.WhatsApp.Entities.OutBoxRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OutBoxRequestRepo extends JpaRepository<OutBoxRequest,Long> {
    @Query("SELECT u FROM OutBoxRequest u WHERE u.status = :aNew")
    Optional<List<OutBoxRequest>> findAllByStatus(String aNew);
}
