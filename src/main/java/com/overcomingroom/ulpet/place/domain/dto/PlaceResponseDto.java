package com.overcomingroom.ulpet.place.domain.dto;

import com.overcomingroom.ulpet.place.domain.entity.Category;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlaceResponseDto {

    private String placeName; // 장소 명

    private String placeDescription; // 장소 설명

    private String address; // 주소

    private Double lat; // 위도

    private Double lon; // 경도

    @LastModifiedDate
    private LocalDateTime updatedAt; // 업데이트 일

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public static PlaceResponseDto toEntity(Place place) {
        return PlaceResponseDto.builder()
                .placeName(place.getPlaceName())
                .placeDescription(place.getPlaceDescription())
                .address(place.getAddress())
                .lat(place.getLat())
                .lon(place.getLat())
                .build();
    }

}
