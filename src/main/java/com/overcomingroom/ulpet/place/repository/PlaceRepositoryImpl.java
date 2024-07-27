package com.overcomingroom.ulpet.place.repository;

import com.overcomingroom.ulpet.place.domain.Category;
import com.overcomingroom.ulpet.place.domain.dto.PlaceResponseDto;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.repository.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;

import java.util.ArrayList;
import java.util.List;

import static com.overcomingroom.ulpet.certification.domain.entity.QCertification.certification;
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
                                         String searchKeyword,
                                         boolean certificationSort) {

        JPAQuery<Place> where = selectFrom(place)
                .where(
                        categoryFilter(category),
                        featureFilter(feature),
                        searchFilter(searchKeyword)
                );

        if (certificationSort) {
            where.orderBy(isCertificationSort());
        }

        List<Place> placeList = where.fetch();

        List<PlaceResponseDto> placeResponseDtoList = new ArrayList<>();

        for (Place place : placeList) {
            placeResponseDtoList.add(PlaceResponseDto.of(place));
        }

        return placeResponseDtoList;
    }

    /**
     * 새로 등록된 장소를 반환합니다.
     *
     * @param numberOfPlaces 보여 줄 장소 수
     * @return 새로 등록된 장소 정보 List<PlaceResponseDto>
     */
    @Override
    public List<PlaceResponseDto> newRegisterPlaces(Long numberOfPlaces) {

        JPAQuery<Place> placeJPAQuery = selectFrom(place)
                .orderBy(place.createdAt.desc());// 최근 등록 순 정렬

        // 만약 numberOfPlaces 가 null이 아니라면 limit 설정
        if (numberOfPlaces != null) {
            placeJPAQuery.limit(numberOfPlaces);
        }

        List<Place> placeList = placeJPAQuery.fetch();

        // 보여 줄 장소 수
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
        return place.certifications.any().certificationFeatures.any().feature.feature.eq(feature);
    }

    /**
     * 인증 이 많은 순서 대로 결과를 정렬합니다.
     *
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> isCertificationSort() {
        return new OrderSpecifier<>(Order.DESC, Expressions.asNumber(countCertification()));
    }

    /**
     * 인증 정렬 서브 쿼리
     *
     * @return JPAQuery
     */
    private JPAQuery<Long> countCertification() {
        return getQueryFactory().select(certification.count())
                .from(certification)
                .where(certification.place.eq(place));
    }

}
