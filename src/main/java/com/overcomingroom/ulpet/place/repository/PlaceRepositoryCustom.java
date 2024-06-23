package com.overcomingroom.ulpet.place.repository;

import com.overcomingroom.ulpet.place.domain.Category;
import com.overcomingroom.ulpet.place.domain.dto.PlaceResponseDto;

import java.util.List;

public interface PlaceRepositoryCustom {

    List<PlaceResponseDto> search(Category category, // 카테고리
                                  String feature, // 특징
                                  String searchKeyword); // 검색어

    List<PlaceResponseDto> newRegisterPlaces(Long numberOfPlaces);
}
