package com.overcomingroom.ulpet.banner.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bannerImage; // 이미지

    private String explanation; // 이미지 설명

    @Enumerated(EnumType.STRING) //  NOT_LAUNCHED 등으로 저장
    private BannerStatus launch; // 현재 개시 상태

}
