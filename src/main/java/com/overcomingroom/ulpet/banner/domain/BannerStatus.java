package com.overcomingroom.ulpet.banner.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BannerStatus {

    NOT_LAUNCHED("개시 전"),
    LAUNCHING("개시 중"),
    CLOSED ("개시 끝");

    private final String status;
}
