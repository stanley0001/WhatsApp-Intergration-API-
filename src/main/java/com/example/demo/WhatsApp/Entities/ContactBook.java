package com.example.demo.WhatsApp.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContactBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true,nullable = false)
    private Long id;
    private String name;
    private String contactPhone;
    private String contactEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Contacts> contacts;
}
