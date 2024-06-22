package com.overcomingroom.ulpet.place.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "test"})
public class PlaceDataLoader {

    private final OpenAPIService openAPIService;

    /**
     * 매일 오전 9시 api 호출 및 저장 로직 실행
     */
//    @PostConstruct
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void dataProcess() {

        // tourApi 호출
        openAPIService.tourApiProcess();

        // 반려동물 동반가능 시설 호출
        openAPIService.petsAllowedApiProcess();

    }

}
