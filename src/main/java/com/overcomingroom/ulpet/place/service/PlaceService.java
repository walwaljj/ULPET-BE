package com.overcomingroom.ulpet.place.service;

import com.overcomingroom.ulpet.certification.domain.entity.Certification;
import com.overcomingroom.ulpet.certification.domain.entity.CertificationFeature;
import com.overcomingroom.ulpet.certification.service.CertificationService;
import com.overcomingroom.ulpet.exception.CustomException;
import com.overcomingroom.ulpet.exception.ErrorCode;
import com.overcomingroom.ulpet.member.service.WishlistService;
import com.overcomingroom.ulpet.place.domain.Category;
import com.overcomingroom.ulpet.place.domain.dto.PlaceResponseDto;
import com.overcomingroom.ulpet.place.domain.entity.Feature;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.domain.entity.PlaceImage;
import com.overcomingroom.ulpet.place.repository.PlaceImageRepository;
import com.overcomingroom.ulpet.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;
    private final WishlistService wishlistService;
    private final CertificationService certificationService;

    /**
     * 통합 검색을 합니다.
     * 카테고리, 장소 특징, 키워드(장소 명, 주소) 검색이 가능합니다.
     *
     * @param category      카테고리
     * @param feature       특징
     * @param searchKeyword 장소명 또는 주소
     * @return List<PlaceResponseDto>
     */
    public List<PlaceResponseDto> searchPlaces(Category category,
                                               String feature,
                                               String searchKeyword,
                                               boolean certificationSort) {
        List<PlaceResponseDto> placeList = placeRepository.search(category, feature, searchKeyword, certificationSort);

        // 이미지 매핑
        for (PlaceResponseDto placeResponseDto : placeList) {
            String imageUrl = getImageUrlByPlaceId(placeResponseDto.getId());
            placeResponseDto.setPlaceImageUrl(imageUrl);
            log.info(placeResponseDto.getPlaceImageUrl());
        }

        return placeList;
    }

    /**
     * placeId를 이욯해 imageUrl정보를 찾아 반환합니다.
     *
     * @param placeId placeId
     * @return imageUrl
     */
    private String getImageUrlByPlaceId(Long placeId) {
        PlaceImage placeImage = placeImageRepository.findByPlaceId(placeId);
        if (placeImage == null || placeImage.getImageUrl() == null || placeImage.getImageUrl().isEmpty()) {
            return null;
        }
        return placeImage.getImageUrl();
    }


    /**
     * placeId를 이욯해 장소 상세를 반환합니다.
     *
     * @param placeId placeId
     * @return PlaceResponseDto
     */
    public PlaceResponseDto getPlaceDetail(Long placeId) {

        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

        PlaceResponseDto placeResponseDto = PlaceResponseDto.of(place);
        placeResponseDto.setPlaceImageUrl(getImageUrlByPlaceId(placeId));

        // 특징 카운트
        List<Certification> certifications = place.getCertifications();
        Map<String, Integer> featureCountMap = new HashMap<>();

        for (Certification certification : certifications) {
            List<CertificationFeature> certificationFeatures = certification.getCertificationFeatures();
            // 장소의 특징을 가져옴
            for (CertificationFeature certificationFeature : certificationFeatures) {

                // map 에 추가하며 count 를 올림
                // 1. 만약 key에 placeFeature 가 이미 추가되어 있다면 key를 추가하지 않고 value를 +1 올림
                Feature feature = certificationFeature.getFeature();
                if (featureCountMap.containsKey(feature.getFeature().toString())) {
                    featureCountMap.put(feature.getFeature().toString(), featureCountMap.get(feature.getFeature()) + 1);
                }
                //2. placeFeature 가 없다면 key와 value를 추가함.
                else {
                    featureCountMap.put(feature.getFeature().toString(), 1);
                }

            }
        }

        placeResponseDto.setFeatureAndCount(featureCountMap);

        return placeResponseDto;
    }


    /**
     * 새로 등록된 장소를 반환합니다.
     *
     * @param numberOfPlaces 보여줄 장소 수
     * @return 새로 등록된 장소 정보 List<PlaceResponseDto>
     */
    public List<PlaceResponseDto> newRegisterPlaces(Long numberOfPlaces) {

        List<PlaceResponseDto> placeList = placeRepository.newRegisterPlaces(numberOfPlaces);

        // 이미지 매핑
        for (PlaceResponseDto placeResponseDto : placeList) {
            String imageUrl = getImageUrlByPlaceId(placeResponseDto.getId());
            placeResponseDto.setPlaceImageUrl(imageUrl);
            log.info(placeResponseDto.getPlaceImageUrl());
        }

        return placeList;
    }

    /**
     * 장소를 삭제합니다.
     */
    @Transactional
    public void deletePlace(Long placeId) throws UnsupportedEncodingException {

        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

        // 위시리스트에서 장소 삭제
        wishlistService.PreRemovePlaceFromWishList(place);

        List<Certification> certifications = place.getCertifications();

        if (!certifications.isEmpty()) {
            for (Certification certification : certifications) {
                // 인증 삭제
                certificationService.certificationDelete(certification.getId(), certification.getMember().getUsername(), false);
            }
        }

        // 장소 삭제
        placeRepository.delete(place);
    }
}
