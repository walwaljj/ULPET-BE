package com.overcomingroom.ulpet.certification.domain.dto;

import com.overcomingroom.ulpet.place.domain.entity.Feature;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CertificationRequestDto {

    private Long memberId; // 사용자 ID

    private Long placeId; // 장소 ID

    private String address; // 장소 주소

    private String placeName; // 장소 이름

    public String categoryName; // 카테고리 이름

    private List<Feature> featureList; // 특징

    private boolean useImage; // 사진 사용 여부

}
