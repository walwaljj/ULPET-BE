package com.overcomingroom.ulpet.member.domain.dto;

import lombok.Builder;

@Builder
public record TempPasswordAndNickname(
    String tempPassword,
    String nickname
) {

}
