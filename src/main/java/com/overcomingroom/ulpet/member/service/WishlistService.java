package com.overcomingroom.ulpet.member.service;

import com.overcomingroom.ulpet.exception.CustomException;
import com.overcomingroom.ulpet.exception.ErrorCode;
import com.overcomingroom.ulpet.member.domain.dto.WishlistResponseDto;
import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.member.domain.entity.Wishlist;
import com.overcomingroom.ulpet.member.repository.MemberRepository;
import com.overcomingroom.ulpet.member.repository.WishlistRepository;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class WishlistService {

    private final PlaceRepository placeRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final WishlistRepository wishlistRepository;


    /**
     * 장소를 위시리스트에 추가합니다.
     *
     * @param placeId
     * @param memberId
     * @return WishlistResponseDto
     */
    public WishlistResponseDto placeAddedToWishlist(Long placeId, Long memberId, String username) {

        // 접근 권한 확인 후 Member 반환.
        MemberEntity member = memberService.verifyMemberAccessAndRetrieve(memberId, username);

        // 장소 찾기
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

        // 위시리스트 찾기
        Optional<Wishlist> optionalWishlist = wishlistRepository.findById(memberId);

        Wishlist wishlist;

        if (!optionalWishlist.isPresent()) {
            wishlist = wishlistRepository.save(Wishlist.builder()
                    .memberId(memberId)
                    .place(new ArrayList<>())
                    .build());

        } else {
            wishlist = optionalWishlist.get();
            // 저장 된 장소인지 확인
            if (isAlreadySave(wishlist.getPlace(), place))
                throw new CustomException(ErrorCode.PLACE_ALREADY_SAVED_TO_WISHLIST);
        }

        wishlist.getPlace().add(place);

        // 저장
        wishlistRepository.save(wishlist);

        member.setWishList(wishlist);
        memberRepository.save(member);

        return WishlistResponseDto.of(place, member);
    }

    /**
     * 장소를 위시리스트에서 삭제합니다.
     *
     * @param placeId
     * @param memberId
     */
    public void placeRemovedFromWishlist(Long placeId, Long memberId, String username) {
        // 접근 권한 확인 후 Member 반환.
        MemberEntity member = memberService.verifyMemberAccessAndRetrieve(memberId, username);
        // 장소 찾기
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
        // 위시리스트 찾기
        Optional<Wishlist> optionalWishlist = wishlistRepository.findById(memberId);

        if (!optionalWishlist.isPresent()) {
            throw new CustomException(ErrorCode.WISHLIST_EMPTY);
        }

        Wishlist wishlist = member.getWishList();

        // 저장 된 장소인지 확인
        if (!isAlreadySave(wishlist.getPlace(), place))
            throw new CustomException(ErrorCode.PLACE_NOT_SAVED_IN_WISHLIST);

        //member 의 wishlist 에서 삭제
        wishlist.getPlace().remove(place);

        // 저장
        wishlistRepository.save(member.getWishList());
    }

    /**
     * 삭제된 장소를 포함하고있던 위시리스트를 찾고 삭제합니다.
     *
     * @param place 삭제 장소
     */
    public void PreRemovePlaceFromWishList(Place place) {

        List<Wishlist> wishlistRepositoryAll = wishlistRepository.findAll();

        for (Wishlist wishlist : wishlistRepositoryAll) {
            if (isAlreadySave(wishlist.getPlace(), place)) {
                wishlist.getPlace().remove(place);
                wishlistRepository.save(wishlist);
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
        MemberEntity member = memberService.verifyMemberAccessAndRetrieve(memberId, username);

        // 위시리스트 찾기
        Optional<Wishlist> optionalWishlist = wishlistRepository.findById(memberId);

        if (!optionalWishlist.isPresent()) {
            throw new CustomException(ErrorCode.WISHLIST_EMPTY);
        }

        Wishlist wishList = optionalWishlist.get();

        return wishList.getPlace().stream()
                .map(place -> WishlistResponseDto.of(place, member))
                .toList();
    }
}
