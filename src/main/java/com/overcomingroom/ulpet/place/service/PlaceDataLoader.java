package com.overcomingroom.ulpet.place.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlaceDataLoader {

    private final OpenAPIService openAPIService;

    /**
     * 의존성 주입 후 api 호출 및 저장 로직 실행
     */
    @PostConstruct
    public void dataProcess() {

        // tourApi 호출
        openAPIService.tourApiProcess();

        // 반려동물 동반가능 시설 호출
        openAPIService.petsAllowedApiProcess();

    }

}
