package com.overcomingroom.ulpet.place.repository;

import com.overcomingroom.ulpet.place.domain.Category;
import com.overcomingroom.ulpet.place.domain.dto.PlaceResponseDto;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import java.util.ArrayList;
import java.util.List;

import static com.overcomingroom.ulpet.place.domain.entity.QPlace.place;

public class PlaceRepositoryImpl extends Querydsl4RepositorySupport implements PlaceRepositoryCustom {

    public PlaceRepositoryImpl() {
        super(Place.class);
    }

    /**
     * 통합 검색을 진행합니다.
     *
     * @param category      카테고리
     * @param feature       특징
     * @param searchKeyword 키워드
     * @return 검색 결과 List<PlaceResponseDto>
     */
    @Override
    public List<PlaceResponseDto> search(Category category,
                                         String feature,
                                         String searchKeyword) {

        List<Place> placeList = selectFrom(place)
                .where(
                        categoryFilter(category),
                        featureFilter(feature),
                        searchFilter(searchKeyword)
                ).fetch();

        List<PlaceResponseDto> placeResponseDtoList = new ArrayList<>();

        for (Place place : placeList) {
            placeResponseDtoList.add(PlaceResponseDto.of(place));
        }

        return placeResponseDtoList;
    }

    private BooleanExpression categoryFilter(Category category) {
        return category != null ? place.category.eq(category) : null;
    }

    /**
     * 키워드 검색
     */
    private BooleanExpression searchFilter(String searchKeyword) {

        if (searchKeyword == null || searchKeyword.isEmpty()) {
            return null; // 검색 키워드가 없을 때 필터링하지 않음
        }

        // 디폴트로 타이틀 + 내용 전체 검색
        return place.placeName.containsIgnoreCase(searchKeyword).or(place.address.containsIgnoreCase(searchKeyword));
    }

    /**
     * 특징 검색
     */
    private BooleanExpression featureFilter(String feature) {

        if (feature == null || feature.isEmpty()) {
            return null; // 검색할 특징이 없을 때 필터링하지 않음
        }

        return place.features.any().feature.eq(feature);
    }

}
