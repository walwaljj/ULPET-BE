package com.overcomingroom.ulpet.place.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Category {

    // TOUR API
    TOURISM(12,"관광"),
    CULTURAL(14,"문화 시설"),
    FESTIVAL(15,"축제/공연/행사"),
    LEISURE_SPORTS(28,"레포츠"),
    ACCOMMODATION(32,"숙소"),
    SHOPPING(38, "쇼핑"),
    FOOD(39, "음식/카페"),

    // 반려동물 동반 가능 문화시설 위치
    ANIMAL_GROOMING_SALON(100,"미용"),
    ANIMAL_HOSPITAL(200,"동물병원"),
    ANIMAL_PHARMACY(201,"동물약국"),
    ANIMAL_GSHOPPING(300,"반려동물용품");

    private final int code;
    private final String categoryName;

    public static Category findByCode(int code) {
        for (Category category : values()) {
            if (category.getCode() == code) {
                return category;
            }
        }
        return null;
    }

    public static Category findByCategoryName(String categoryName) {
        for (Category category : values()) {
            if (category.getCategoryName().equals(categoryName)) {
                return category;
            }
        }
        return null;
    }
}
