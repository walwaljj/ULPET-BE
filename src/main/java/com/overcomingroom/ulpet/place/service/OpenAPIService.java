package com.overcomingroom.ulpet.place.service;

import com.overcomingroom.ulpet.base.BaseEntityDateTimeUtil;
import com.overcomingroom.ulpet.place.domain.Category;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.domain.entity.PlaceImage;
import com.overcomingroom.ulpet.place.repository.PlaceImageRepository;
import com.overcomingroom.ulpet.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpenAPIService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;

    @Value("${tour-api.api-key}")
    private String serviceKey;

    @Value("${tour-api.service-url}")
    private String tourApiUrl;

    @Value("${system-id}")
    private String systemId;
    private final static String TOUR_API_DATETIME_PATTERN = "yyyyMMddHHmmss";

    /**
     * tour-api 호출
     *
     * @return (string) json data
     */
    public String getTourDataMono() {
        WebClient webClient = WebClient.builder()
                .baseUrl(tourApiUrl)
                .defaultHeader("Content-Type", "application/json;charset=utf-8")
                .build();

        String dataType = "json";

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/searchKeyword1")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", "200")
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "AppTest")
                        .queryParam("_type", dataType)
                        .queryParam("listYN", "Y")
                        .queryParam("arrange", "A")
                        .queryParam("keyword", "울산")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .retry(3)
                .block();

    }


    /**
     * tour api 에서 place 객체로 변환 후 저장합니다.
     *
     * @param tourApiData
     */
    @Transactional
    public void convertFromTourApiToPlace(String tourApiData) {

        JSONObject jsonObject = new JSONObject(tourApiData).getJSONObject("response").getJSONObject("body").getJSONObject("items");
        JSONArray jsonArray = jsonObject.getJSONArray("item");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonData = jsonArray.getJSONObject(i);

            // 문자열을 LocalDateTime으로 변환
            LocalDateTime createdtime = BaseEntityDateTimeUtil.localDateTimeParse(jsonData.get("createdtime").toString(), TOUR_API_DATETIME_PATTERN);
            LocalDateTime updatedtime = BaseEntityDateTimeUtil.localDateTimeParse(jsonData.get("modifiedtime").toString(), TOUR_API_DATETIME_PATTERN);

            Place place = placeRepository.save(Place.builder()
                    .placeName(jsonData.get("title").toString())
                    .contentId(Long.parseLong(jsonData.get("contentid").toString()))
                    .address(jsonData.get("addr1").toString())
                    .lat(Place.roundValue(Double.parseDouble(jsonData.get("mapx").toString())))
                    .lon(Place.roundValue(Double.parseDouble(jsonData.get("mapy").toString())))
                    .category(Category.findByCode(Integer.parseInt(jsonData.get("contenttypeid").toString())))
                    .createdAt(createdtime)
                    .updatedAt(updatedtime)
                    .createdBy(Long.valueOf(systemId))
                    .build());

            placeImageRepository.save(PlaceImage.builder()
                    .placeId(place.getId())
                    .imageUrl(jsonData.get("firstimage").toString()).build());
        }

    }

}
