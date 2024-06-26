package com.overcomingroom.ulpet.auth.domain.dto;

import lombok.Builder;

@Builder
public record JwtResponseDto(
    String accessToken,
    String refreshToken
) {

}
