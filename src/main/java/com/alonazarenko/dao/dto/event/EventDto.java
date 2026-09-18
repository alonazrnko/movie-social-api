package com.alonazarenko.dao.dto.event;

import lombok.Data;

@Data
public class EventDto {
    private long eventId;
    private long timestamp;
    private long userId;
    private String eventType;
    private String operation;
    private long entityId;
}
