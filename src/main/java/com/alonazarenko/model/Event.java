package com.alonazarenko.model;

import com.alonazarenko.model.enums.EventOperation;
import com.alonazarenko.model.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private Long eventId;
    private Long timestamp;
    private Long userId;
    private EventType eventType;
    private EventOperation operation;
    private Long entityId;
}
