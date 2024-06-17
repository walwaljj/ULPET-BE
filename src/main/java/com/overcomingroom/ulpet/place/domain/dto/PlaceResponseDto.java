package com.overcomingroom.ulpet.place.domain.dto;

import com.overcomingroom.ulpet.place.domain.Category;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.domain.entity.PlaceImage;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponseDto {

    private Long contentId;

    private String placeName; // 장소 명

    private String placeDescription; // 장소 설명

    private String address; // 주소

    private Double lat; // 위도

    private Double lon; // 경도

    @Enumerated(EnumType.STRING)
    private Category category; // 카테고리

    private PlaceImage placeImage; // 장소 이미지

    public static PlaceResponseDto toEntity(Place place) {
        return PlaceResponseDto.builder()
                .contentId(place.getContentId())
                .placeName(place.getPlaceName())
                .placeDescription(place.getPlaceDescription())
                .address(place.getAddress())
                .lat(place.getLat())
                .lon(place.getLat())
                .category(place.getCategory())
                .build();
    }

}
