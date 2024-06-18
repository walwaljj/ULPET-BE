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
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpenAPIService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;

    @Value("${tour-api.decoding-api-key}")
    private String tourApiServiceKey;

    @Value("${tour-api.service-url}")
    private String tourApiUrl;

    @Value("${pet-allowed-information-api.decoding-api-key}")
    private String petAllowedApiServiceKey;

    @Value("${pet-allowed-information-api.service-url}")
    private String petAllowedApiUrl;

    @Value("${system-id}")
    private String systemId;
    private final static String TOUR_API_DATETIME_PATTERN = "yyyyMMddHHmmss";
    private final static String PET_ALLOWED_API_DATETIME_PATTERN = "yyyy-MM-dd";
    static final String DATA_TYPE = "json";

    static int totalData = 0;
    static final int PER_PAGE = 200;
    static int lastPage = Integer.MAX_VALUE; // 가장 큰 수


    /**
     * webClient 를 반환합니다.
     *
     * @param baseUrl api url
     * @return
     */
    private WebClient getWebClient(String baseUrl) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json;charset=utf-8")
                .build();
        return webClient;
    }

    /**
     * 반려동물 동반 정보 api 호출 및 DB저장
     */
    public void petsAllowedApiProcess() {
        List<String> petsAllowedDataMono = getPetsAllowedDataMono();
        convertFromPetsAllowedDataApiToPlace(petsAllowedDataMono);
    }

    /**
     * tour api 호출 및 DB 저장
     */
    public void tourApiProcess() {
        // tourApi 호출
        String tourData = getTourDataMono();

        // tour Api -> place 객체 변환 및 저장
        convertFromTourApiToPlace(tourData);
    }

    /**
     * tour-api 호출
     *
     * @return (string) json data
     */
    public String getTourDataMono() {

        WebClient webClient = getWebClient(tourApiUrl);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/searchKeyword1")
                        .queryParam("serviceKey", tourApiServiceKey)
                        .queryParam("numOfRows", "200")
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "AppTest")
                        .queryParam("_type", DATA_TYPE)
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
     * tour Api 데이터를 파싱하고, 저장합니다.
     *
     * @param tourApiData String 타입의 tourApiData
     */
    @Transactional
    public void convertFromTourApiToPlace(String tourApiData) {

        JSONObject jsonObject = new JSONObject(tourApiData).getJSONObject("response").getJSONObject("body").getJSONObject("items");
        JSONArray jsonArray = jsonObject.getJSONArray("item");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonData = jsonArray.getJSONObject(i);

            // 장소의 주소 정보
            String address = jsonData.get("addr1").toString();

            // contentTypeId를 Category 타입으로 변환
            Category category = Category.findByCode(Integer.parseInt(jsonData.get("contenttypeid").toString()));

            // 주소에 울산광역시가 포함된 정보만을 대상으로 저장
            if (address.contains("울산광역시")) {

                // 문자열을 LocalDateTime으로 변환
                LocalDateTime createdtime = BaseEntityDateTimeUtil.localDateTimeToLocalDateTimeParse(jsonData.get("createdtime").toString(), TOUR_API_DATETIME_PATTERN);
                LocalDateTime updatedtime = BaseEntityDateTimeUtil.localDateTimeToLocalDateTimeParse(jsonData.get("modifiedtime").toString(), TOUR_API_DATETIME_PATTERN);

                // 카테고리를 가지지 않는 데이터 제외
                if (category != null) {
                    Place place = placeRepository.save(Place.builder()
                            .placeName(jsonData.get("title").toString())
                            .contentId(Long.parseLong(jsonData.get("contentid").toString()))
                            .address(address)
                            .lat(Place.roundValue(Double.parseDouble(jsonData.get("mapx").toString())))
                            .lon(Place.roundValue(Double.parseDouble(jsonData.get("mapy").toString())))
                            .category(category)
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
    }

    /**
     * 반려동물 동반 정보 호출
     *
     * @return List<String>
     */
    public List<String> getPetsAllowedDataMono() {
        WebClient webClient = getWebClient(petAllowedApiUrl);

        String block = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("serviceKey", petAllowedApiServiceKey)
                        .queryParam("page", "1")
                        .queryParam("perPage", "1")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 실제 데이터 양
        totalData = parseTotalDataCount(block);
        // 총 페이지 수 계산
        setRange(totalData, PER_PAGE);

        List<String> responseList = new ArrayList<>();

        for (int page = 1; page <= lastPage; page++) {
            String currentPage = String.valueOf(page);
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("serviceKey", petAllowedApiServiceKey)
                            .queryParam("page", currentPage)
                            .queryParam("perPage", PER_PAGE)
//                            .queryParam("returnType", dataType)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            responseList.add(response);
        }

        return responseList;
    }

    /**
     * 반려동물 동반 가능 시설 api에서 place 객체로 변환 후 저장합니다.
     *
     * @param responseList String response List
     */
    @Transactional
    public void convertFromPetsAllowedDataApiToPlace(List<String> responseList) {

        for (String response : responseList) {
            JSONArray jsonArray = new JSONObject(response).getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);

                // 주소에 울산광역시가 포함된 정보만을 대상으로 저장
                if (jsonData.get("시도 명칭").toString().equals("울산광역시")) {
                    String category = jsonData.get("카테고리3").toString();

                    // 필요한 category를 선별
                    if (category.equals("동물병원") ||
                            category.equals("동물약국") ||
                            category.equals("반려동물용품") ||
                            category.equals("미용")) {

                        Category categoryEnum = categoryFilter(category);

                        // 문자열을 LocalDateTime으로 변환
                        LocalDateTime createdtime = BaseEntityDateTimeUtil.localDateToLocalDateTimeParse(jsonData.get("최종작성일").toString(), PET_ALLOWED_API_DATETIME_PATTERN);
                        LocalDateTime updatedtime = BaseEntityDateTimeUtil.localDateToLocalDateTimeParse(jsonData.get("최종작성일").toString(), PET_ALLOWED_API_DATETIME_PATTERN);

                        placeRepository.save(Place.builder()
                                .placeName(jsonData.get("시설명").toString())
                                .address(jsonData.get("도로명주소").toString())
                                .placeDescription(jsonData.get("기본 정보_장소설명").toString())
                                .lat(Place.roundValue(Double.parseDouble(jsonData.get("위도").toString())))
                                .lon(Place.roundValue(Double.parseDouble(jsonData.get("경도").toString())))
                                .category(categoryEnum)
                                .createdAt(createdtime)
                                .updatedAt(updatedtime)
                                .createdBy(Long.valueOf(systemId))
                                .build());
                    }

                }
            }
        }

    }

    /**
     * 카테고리 정보를 통해 데이터를 필터링 합니다.
     *
     * @param categoryName String type CategoryName
     * @return
     */
    private Category categoryFilter(String categoryName) {
        return Category.findByCategoryName(categoryName);
    }


    /**
     * 총 데이터 수를 가져옵니다.
     *
     * @param response 응답
     * @return
     */
    private int parseTotalDataCount(String response) {
        Object totalCount = new JSONObject(response).get("totalCount");
        return Integer.valueOf(totalCount.toString());
    }

    /**
     * page 반복 수를 계산합니다
     *
     * @param actualLastPage 총 데이터 수
     * @param perPage        한 페이지 데이터 양
     */
    private void setRange(int actualLastPage, int perPage) {
        lastPage = actualLastPage % perPage == 0 ? actualLastPage / perPage : actualLastPage / perPage + 1;
    }


}
