package com.alonazarenko.service;

import com.alonazarenko.dao.repository.ReviewLikeRepository;
import com.alonazarenko.dao.repository.ReviewRepository;
import com.alonazarenko.exception.DuplicatedDataException;
import com.alonazarenko.exception.NotFoundException;
import com.alonazarenko.model.ReviewLike;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public void addReaction(Long reviewId, Long userId, boolean isLike) {
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review with id=" + reviewId + " not found"));
        userService.getById(userId);

        Optional<ReviewLike> existingReactionOpt = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId);

        if (existingReactionOpt.isPresent()) {
            ReviewLike existingReaction = existingReactionOpt.get();
            Boolean existingIsLike = existingReaction.getIsLike();

            if (isLike == existingIsLike) {
                String reactionType = isLike ? "like" : "dislike";
                throw new DuplicatedDataException(
                        "User " + userId + " has already given " + reactionType + " to review " + reviewId);
            } else {
                updateReaction(reviewId, userId, isLike);
                String from = existingIsLike ? "like" : "dislike";
                String to = isLike ? "like" : "dislike";
                log.info("User {} changed {} to {} for review {}", userId, from, to, reviewId);
                return;
            }
        }

        ReviewLike reviewLike = new ReviewLike(reviewId, userId, isLike);
        reviewLikeRepository.save(reviewLike);
        updateReviewUseful(reviewId);

        String reactionType = isLike ? "like" : "dislike";
        log.info("User {} has already given {} to review {}", userId, reactionType, reviewId);
    }

    public void addLike(Long reviewId, Long userId) {
        addReaction(reviewId, userId, true);
    }

    public void addDislike(Long reviewId, Long userId) {
        addReaction(reviewId, userId, false);
    }

    public void removeLike(Long reviewId, Long userId) {
        removeReaction(reviewId, userId, true);
        log.info("User {} deleted like to review {}", userId, reviewId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        removeReaction(reviewId, userId, false);
        log.info("User {} deleted dislike to review {}", userId, reviewId);
    }

    private void removeReaction(Long reviewId, Long userId, Boolean expectedType) {
        ReviewLike existingReaction = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new NotFoundException(
                        "User's reaction " + userId + " to the review " + reviewId + " not found"));

        if (!expectedType.equals(existingReaction.getIsLike())) {
            throw new NotFoundException(
                    "Expected " + (expectedType ? "like" : "dislike") +
                            ", found: another reaction");
        }

        reviewLikeRepository.delete(reviewId, userId);

        updateReviewUseful(reviewId);
    }

    private void updateReaction(Long reviewId, Long userId, Boolean newIsLike) {
        ReviewLike reviewLike = new ReviewLike(reviewId, userId, newIsLike);
        reviewLikeRepository.update(reviewLike);

        updateReviewUseful(reviewId);
    }

    public Long getUseful(Long reviewId) {
        long likesCount = reviewLikeRepository.countLikes(reviewId);
        long dislikesCount = reviewLikeRepository.countDislikes(reviewId);
        return likesCount - dislikesCount;
    }

    private void updateReviewUseful(Long reviewId) {
        Long useful = getUseful(reviewId);
        reviewLikeRepository.updateReviewUseful(reviewId, useful);
    }
}
