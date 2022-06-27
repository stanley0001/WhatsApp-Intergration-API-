package com.example.demo.WhatsApp.Repositories;

import com.example.demo.WhatsApp.Entities.ContactBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactBookRepo extends JpaRepository<ContactBook,Long> {
}
