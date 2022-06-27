package com.example.demo.WhatsApp.Repositories;

import com.example.demo.WhatsApp.Entities.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactsRepo extends JpaRepository<Contacts,Long> {
}
