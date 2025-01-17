package com.overcomingroom.ulpet.place.domain.entity;

import com.overcomingroom.ulpet.base.BaseEntity;
import com.overcomingroom.ulpet.certification.domain.entity.Certification;
import com.overcomingroom.ulpet.place.domain.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@SuperBuilder
@DynamicUpdate
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentId; // 컨텐츠 ID (장소 설명 매핑 시 이용)

    private String placeName; // 장소 명

    private String placeDescription; // 장소 설명

    private String address; // 주소

    private Double lat; // 위도

    private Double lon; // 경도

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany
    @Setter
    private List<Certification> certifications = new ArrayList<>();

    /**
     * 위도, 경도를 소숫점 6자리 까지 변환함.
     *
     * @param originalValue 변환 대상 값
     * @return 변환 완료한 값
     */
    public static double roundValue(double originalValue) {
        return Math.floor(originalValue * 1000000) / 1000000.0;
    }

    public void modifyPlace(Place place) {
        this.contentId = place.getContentId();
        this.placeName = place.getPlaceName();
        this.placeDescription = place.getPlaceDescription();
        this.address = place.getAddress();
        this.lat = place.getLat();
        this.lon = place.getLon();
    }
}
