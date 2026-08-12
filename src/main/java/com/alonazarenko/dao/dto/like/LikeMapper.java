package com.alonazarenko.dao.dto.like;

import com.alonazarenko.model.Like;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LikeMapper {
    public static Like mapToLike(NewLikeRequest request) {
        return new Like(request.getFilmId(), request.getUserId());
    }

    public static LikeDto mapToLikeDto(Like like) {
        return new LikeDto(like.getFilmId(), like.getUserId());
    }
}
