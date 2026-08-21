package com.alonazarenko.controller;

import com.alonazarenko.dao.dto.event.EventDto;
import com.alonazarenko.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/feed")
@RequiredArgsConstructor
public class FeedController {

    private final EventService eventService;

    @GetMapping
    public List<EventDto> getUserFeed(@PathVariable long userId) {
        return eventService.getUserFeed(userId);
    }
}
