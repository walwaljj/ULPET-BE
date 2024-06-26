package com.overcomingroom.ulpet.member.domain.dto;

import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WishlistResponseDto {
    private Long placeId;
    private Long memberId;

    public static WishlistResponseDto of(Place place, MemberEntity member) {
        return WishlistResponseDto.builder()
                .placeId(place.getId())
                .memberId(member.getMemberId())
                .build();
    }
}
