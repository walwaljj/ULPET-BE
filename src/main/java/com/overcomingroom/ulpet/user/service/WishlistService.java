package com.overcomingroom.ulpet.user.service;

import com.overcomingroom.ulpet.exception.CustomException;
import com.overcomingroom.ulpet.exception.ErrorCode;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.repository.PlaceRepository;
import com.overcomingroom.ulpet.user.domain.dto.WishlistResponseDto;
import com.overcomingroom.ulpet.user.domain.entity.Users;
import com.overcomingroom.ulpet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class WishlistService {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;


    /**
     * 장소를 위시리스트에 추가합니다.
     *
     * @param placeId
     * @param userId
     * @return WishlistResponseDto
     */
    public WishlistResponseDto placeAddedToWishlist(Long placeId, Long userId) {
        // userID 로 유저 찾기
        Users user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 장소 찾기
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

        List<Place> wishList = user.getWishList();

        // 저장 된 장소인지 확인
        if (isAlreadySave(wishList, place))
            throw new CustomException(ErrorCode.PLACE_ALREADY_SAVED_TO_WISHLIST);

        //user 의 list 에 추가하기
        wishList.add(place);

        // 저장
        userRepository.save(user);

        return WishlistResponseDto.of(place, user);
    }

    /**
     * 장소를 위시리스트에서 삭제합니다.
     *
     * @param placeId
     * @param userId
     */
    public void placeRemovedFromWishlist(Long placeId, Long userId) {
        // userID 로 유저 찾기
        Users user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 장소 찾기
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

        List<Place> wishList = user.getWishList();

        // 저장 된 장소인지 확인
        if(!isAlreadySave(wishList, place))
            throw new CustomException(ErrorCode.PLACE_NOT_SAVED_IN_WISHLIST);

        //user 의 wishlist 에서 삭제
        wishList.remove(place);

        // 저장
        userRepository.save(user);
    }


    /**
     * 유저의 wishlist 에 저장된 장소인지 확인합니다.
     *
     * @param wishList
     * @param place
     * @return boolean ture : 저장됨. false : 저장되지 않은 정보
     */
    private static boolean isAlreadySave(List<Place> wishList, Place place) {
        // 장소 검증
        for (Place stored : wishList) {
            // 기 저장 됨.
            if (stored.getId().equals(place.getId())) {
                return true;
            }
        }

        // 저장 돼있지 않음.
        return false;
    }

    public List<WishlistResponseDto> wishlist(Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return user.getWishList().stream()
                .map(place -> WishlistResponseDto.of(place, user))
                .toList();
    }
}
