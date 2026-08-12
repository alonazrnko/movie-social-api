package com.alonazarenko.dao.dto.mpa;

import com.alonazarenko.model.MpaRating;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MpaMapper {

    public static MpaRating mapToMpaRating(MpaDto request) {
        MpaRating mpa = new MpaRating();
        mpa.setName(request.getName());
        return mpa;
    }

    public static MpaDto mapToMpaDto(MpaRating mpa) {
        MpaDto dto = new MpaDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        return dto;
    }
}

