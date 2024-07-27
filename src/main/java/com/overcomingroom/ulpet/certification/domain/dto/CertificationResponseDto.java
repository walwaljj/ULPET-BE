package com.overcomingroom.ulpet.certification.domain.dto;

import com.overcomingroom.ulpet.certification.domain.entity.Certification;
import com.overcomingroom.ulpet.place.domain.entity.Feature;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CertificationResponseDto {

    private String certificationImageUrl; // 인증 이미지 url

    private String nickname; // 닉네임

    private String placeName; // 장소 ID

    private String address; // 주소

    private List<Feature> featureList; // 특징

    private LocalDateTime localDateTime; // 인증 날짜

    /**
     * entity -> CertificationResponseDto
     *
     * @param certification
     * @return CertificationResponseDto
     */
    public static CertificationResponseDto of(Certification certification, List<Feature> featureList) {
        return CertificationResponseDto.builder()
                .certificationImageUrl(certification.getCertificationImageUrl())
                .nickname(certification.getMember().getNickname())
                .placeName(certification.getPlace().getPlaceName())
                .address(certification.getPlace().getAddress())
                .featureList(featureList)
                .localDateTime(certification.getCreatedAt())
                .build();
    }

    public static CertificationResponseDto of(Certification certification) {
        return CertificationResponseDto.builder()
                .certificationImageUrl(certification.getCertificationImageUrl())
                .nickname(certification.getMember().getNickname())
                .placeName(certification.getPlace().getPlaceName())
                .address(certification.getPlace().getAddress())
                .localDateTime(certification.getCreatedAt())
                .build();
    }
}
