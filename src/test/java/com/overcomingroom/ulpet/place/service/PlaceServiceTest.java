package com.overcomingroom.ulpet.place.service;

import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.member.domain.entity.Wishlist;
import com.overcomingroom.ulpet.member.repository.MemberRepository;
import com.overcomingroom.ulpet.member.repository.WishlistRepository;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PlaceServiceTest {

    @Autowired
    PlaceService placeService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PlaceRepository placeRepository;
    @Autowired
    WishlistRepository wishlistRepository;

    @Test
    void 장소_삭제_테스트() throws UnsupportedEncodingException {

        //given

        MemberEntity member = memberRepository.findById(1L).get();

        Place place1 = placeRepository.findById(1L).get();
        Place place2 = placeRepository.findById(2L).get();

        // 유저 위시리스트 추가
        Wishlist wishlist = wishlistRepository.save(Wishlist.builder()
                .memberId(member.getMemberId())
                .place(List.of(place1, place2))
                .build());

        wishlistRepository.save(wishlist);

        member.setWishList(wishlist);
        memberRepository.save(member);

        //when
        Long id = place1.getId();
        placeService.deletePlace(id);

        //then
        assertThat(wishlist.getPlace())
                .extracting("id")
                .doesNotContain(place1.getId());

        assertThat(wishlist.getPlace())
                .extracting("id")
                .contains(place2.getId());

        assertThat(wishlist.getPlace().size()).isEqualTo(1);
    }
}