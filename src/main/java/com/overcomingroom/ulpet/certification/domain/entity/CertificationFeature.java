package com.overcomingroom.ulpet.certification.domain.entity;

import com.overcomingroom.ulpet.place.domain.entity.Feature;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// N : N 중간 테이블
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "certification_id")
    private Certification certification;

    @ManyToOne
    @JoinColumn(name = "feature_feature")
    private Feature feature;

    public static CertificationFeature certateCertificationFeature(Certification certification, Feature feature) {
        return CertificationFeature.builder()
                .certification(certification)
                .feature(feature)
                .build();
    }
}
