package com.overcomingroom.ulpet.member.service;

import com.overcomingroom.ulpet.exception.CustomException;
import com.overcomingroom.ulpet.exception.ErrorCode;
import com.overcomingroom.ulpet.member.domain.dto.WishlistResponseDto;
import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.member.repository.MemberRepository;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class WishlistService {

    private final PlaceRepository placeRepository;
    private final MemberRepository memberRepository;


    /**
     * 장소를 위시리스트에 추가합니다.
     *
     * @param placeId
     * @param memberId
     * @return WishlistResponseDto
     */
    public WishlistResponseDto placeAddedToWishlist(Long placeId, Long memberId, String username) {
        // 접근 권한 확인 후 Member 반환.
        MemberEntity member = verifyMemberAccessAndRetrieve(memberId, username);

        // 장소 찾기
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

        List<Place> wishList = member.getWishList();

        // 저장 된 장소인지 확인
        if (isAlreadySave(wishList, place))
            throw new CustomException(ErrorCode.PLACE_ALREADY_SAVED_TO_WISHLIST);

        //member 의 list 에 추가하기
        wishList.add(place);

        // 저장
        memberRepository.save(member);

        return WishlistResponseDto.of(place, member);
    }

    /**
     * 회원 접근 권한 확인, 확인 후 회원 정보 반환
     *
     * @param memberId
     * @param username
     * @return 회원 정보
     */
    private MemberEntity verifyMemberAccessAndRetrieve(Long memberId, String username) {
        MemberEntity member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!memberRepository.findByUsername(username).get().equals(member)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        return member;
    }

    /**
     * 장소를 위시리스트에서 삭제합니다.
     *
     * @param placeId
     * @param memberId
     */
    public void placeRemovedFromWishlist(Long placeId, Long memberId, String username) {
        // 접근 권한 확인 후 Member 반환.
        MemberEntity member = verifyMemberAccessAndRetrieve(memberId, username);
        // 장소 찾기
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

        List<Place> wishList = member.getWishList();

        // 저장 된 장소인지 확인
        if (!isAlreadySave(wishList, place))
            throw new CustomException(ErrorCode.PLACE_NOT_SAVED_IN_WISHLIST);

        //member 의 wishlist 에서 삭제
        wishList.remove(place);

        // 저장
        memberRepository.save(member);
    }

    /**
     * 삭제된 장소를 포함하고 있던 멤버를 찾고, 위시리스트에서 장소를 삭제합니다.
     *
     * @param place 삭제 장소
     */
    public void PreRemovePlaceFromWishList(Place place) {
        List<MemberEntity> memberList = memberRepository.findAll();

        for (MemberEntity member : memberList) {
            List<Place> wishList = member.getWishList();
            if (isAlreadySave(wishList, place)) {
                wishList.remove(place);
                memberRepository.save(member);
            }
        }
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

    public List<WishlistResponseDto> wishlist(Long memberId, String username) {
        // 접근 권한 확인 후 Member 반환.
        MemberEntity member = verifyMemberAccessAndRetrieve(memberId, username);

        return member.getWishList().stream()
                .map(place -> WishlistResponseDto.of(place, member))
                .toList();
    }
}
