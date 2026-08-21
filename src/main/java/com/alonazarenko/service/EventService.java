package com.alonazarenko.service;

import com.alonazarenko.dao.dto.event.EventDto;
import com.alonazarenko.dao.dto.event.EventMapper;
import com.alonazarenko.dao.repository.EventRepository;
import com.alonazarenko.model.Event;
import com.alonazarenko.model.enums.EventOperation;
import com.alonazarenko.model.enums.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public void addEvent(long userId,
                         EventType eventType,
                         EventOperation operation,
                         long entityId) {

        Event event = new Event();
        event.setTimestamp(Instant.now().toEpochMilli());
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setEntityId(entityId);

        eventRepository.addEvent(event);
    }

    public List<EventDto> getUserFeed(long userId) {
        return eventRepository.getUserFeed(userId).stream()
                .map(eventMapper::mapToEventDto)
                .toList();
    }
}
