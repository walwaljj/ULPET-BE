package com.overcomingroom.ulpet.certification.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.overcomingroom.ulpet.base.BaseEntityMember;
import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Certification extends BaseEntityMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String certificationImageUrl;

    @ManyToOne
    private MemberEntity member;

    @ManyToOne
    private Place place;

    private boolean useImage;

    @JsonIgnore
    @Setter
    private Float familiarity;

    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    @Setter
    private List<CertificationFeature> certificationFeatures = new ArrayList<>();


}
