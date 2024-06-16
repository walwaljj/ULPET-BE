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
     * 의존성 주입 후
     */
    @PostConstruct
    public void dataProcess() {

        // tourApi 호출
        String tourData = openAPIService.getTourDataMono();

        // place 객체 변환 및 저장
        openAPIService.convertFromTourApiToPlace(tourData);
    }

}
