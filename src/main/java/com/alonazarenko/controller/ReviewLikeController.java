package com.alonazarenko.controller;

import com.alonazarenko.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews/{reviewId}")
@RequiredArgsConstructor
@Slf4j
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;

    @PutMapping("/like/{userId}")
    public void addLike(@PathVariable Long reviewId,
                        @PathVariable Long userId) {
        log.info("PUT /reviews/{}/like/{} - adding like to review", reviewId, userId);
        reviewLikeService.addLike(reviewId, userId);
    }

    @PutMapping("/dislike/{userId}")
    public void addDislike(@PathVariable Long reviewId,
                           @PathVariable Long userId) {
        log.info("PUT /reviews/{}/dislike/{} - adding dislike to review", reviewId, userId);
        reviewLikeService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/like/{userId}")
    public void removeLike(@PathVariable Long reviewId,
                           @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/like/{} - removing like from review", reviewId, userId);
        reviewLikeService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/dislike/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDislike(@PathVariable Long reviewId,
                              @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/dislike/{} - removing dislike from review", reviewId, userId);
        reviewLikeService.removeDislike(reviewId, userId);
    }
}
