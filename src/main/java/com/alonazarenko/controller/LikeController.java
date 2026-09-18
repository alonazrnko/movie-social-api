package com.alonazarenko.controller;

import com.alonazarenko.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        likeService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        likeService.removeLike(id, userId);
    }
}
