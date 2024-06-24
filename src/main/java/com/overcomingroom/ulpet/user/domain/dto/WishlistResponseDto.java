package com.overcomingroom.ulpet.user.domain.dto;

import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.user.domain.entity.Users;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WishlistResponseDto {
    private Long placeId;
    private Long userId;

    public static WishlistResponseDto of(Place place, Users user){
        return WishlistResponseDto.builder()
                .placeId(place.getId())
                .userId(user.getId())
                .build();
    }
}
