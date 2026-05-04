package com.vivaeventos.event_service.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Event {
    
    private String name;
    private String description;
    private LocalDateTime date;
    private String Address;
    private UUID eventId;

    
}
