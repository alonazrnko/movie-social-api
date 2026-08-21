package com.alonazarenko.dao.dto.event;

import com.alonazarenko.model.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public EventDto mapToEventDto(Event event) {
        EventDto eventDto = new EventDto();

        eventDto.setEventId(event.getEventId());
        eventDto.setTimestamp(event.getTimestamp());
        eventDto.setUserId(event.getUserId());
        eventDto.setEventType(event.getEventType().name());
        eventDto.setOperation(event.getOperation().name());
        eventDto.setEntityId(event.getEntityId());

        return eventDto;
    }
}