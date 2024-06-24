package com.overcomingroom.ulpet.member.domain.dto;

import lombok.Builder;

@Builder
public record MemberInfoResponseDto(
    Long memberId,
    String username,
    String nickname,
    String profileImage,
    Float familiarity
) {

}
