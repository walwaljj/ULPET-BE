package com.overcomingroom.ulpet.place.service;

import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.member.repository.MemberRepository;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    void 장소_삭제_테스트() {

        //given

        MemberEntity member = memberRepository.findById(1L).get();

        List<Place> wishList = member.getWishList();

        Place place1 = placeRepository.findById(1L).get();
        Place place2 = placeRepository.findById(2L).get();
        wishList.add(place1);
        wishList.add(place2);
        memberRepository.save(member);

        //when
        Long id = place1.getId();
        placeService.deletePlace(id);

        //then
        assertThat(wishList)
                .extracting("id")
                .doesNotContain(place1.getId());

        assertThat(wishList)
                .extracting("id")
                .contains(place2.getId());

        assertThat(wishList.size()).isEqualTo(1);
    }
}