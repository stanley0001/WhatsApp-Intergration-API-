package com.example.demo.WhatsApp.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Consumer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String token;
    private String instance;
    private String instanceId;
    private String callBackUrl;
    private String consumerBalance;
    @OneToMany(fetch = FetchType.EAGER)
    private List<ContactBook> contactBooks;

}
