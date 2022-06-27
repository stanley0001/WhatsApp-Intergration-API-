package com.example.demo.WhatsApp.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WhatsAppBody {
    private String messaging_product;
    private boolean preview_url;
    private String recipient_type;
    private String to;
    private String type;
    WhatsAppText text;
}
