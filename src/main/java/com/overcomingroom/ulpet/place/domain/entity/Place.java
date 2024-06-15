package com.overcomingroom.ulpet.place.domain.entity;

import com.overcomingroom.ulpet.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Builder
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placeName; // 장소 명

    private String placeDescription; // 장소 설명

    private String address; // 주소

    private Double lat; // 위도

    private Double lon; // 경도

}
