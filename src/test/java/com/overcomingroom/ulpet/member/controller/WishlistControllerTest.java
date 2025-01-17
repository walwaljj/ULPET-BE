package com.overcomingroom.ulpet.member.controller;

import com.overcomingroom.ulpet.auth.domain.dto.JwtResponseDto;
import com.overcomingroom.ulpet.exception.CustomException;
import com.overcomingroom.ulpet.exception.ErrorCode;
import com.overcomingroom.ulpet.member.domain.dto.LoginRequestDto;
import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.member.domain.entity.Wishlist;
import com.overcomingroom.ulpet.member.repository.MemberRepository;
import com.overcomingroom.ulpet.member.repository.WishlistRepository;
import com.overcomingroom.ulpet.member.service.MemberService;
import com.overcomingroom.ulpet.member.service.WishlistService;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.repository.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
class WishlistControllerTest {


    @Autowired
    MockMvc mockMvc;
    @Autowired
    PlaceRepository placeRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    WishlistService wishlistService;
    @Autowired
    MemberService memberService;
    @Autowired
    WishlistRepository wishlistRepository;
    MemberEntity test1;
    String accessToken = "accessToken";

    @BeforeEach
    void init() {

        String username = "test1@ulpet.com";
        test1 = memberRepository.findByUsername(username).get();

        JwtResponseDto login = memberService.login(new LoginRequestDto(username, "abc1234!"));

        accessToken = login.accessToken();

        Long placeId = 1L;
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

        // 유저 위시리스트 추가
        Wishlist wishlist = wishlistRepository.save(Wishlist.builder()
                .memberId(test1.getMemberId())
                .place(List.of(place))
                .build());
        test1.setWishList(wishlist);
        memberRepository.save(test1);
    }

    @DisplayName("위시 리스트 추가 테스트")
    @Test
    void 위시리스트_추가() throws Exception {

        Long placeId = 2L;

        Long memberId = test1.getMemberId();

        List<Place> wishlist = wishlistRepository.findById(memberId).get().getPlace();

        // 유저의 위시리스트에 장소가 없는게 맞는지 확인
        assertThat(wishlist)
                .extracting("id")
                .doesNotContain(placeId);

        // when 장소 추가
        mockMvc.perform(post("/v1/places/" + placeId + "/wishlist")
                        .header("Authorization", "Bearer " + accessToken) // 헤더에 Authorization 값을 추가
                        .param("memberId", String.valueOf(memberId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.placeId").value(placeId))
                .andDo(print());

        // then 유저의 위시리스트에 장소가 추가되었는지 확인
        assertThat(wishlist)
                .extracting("id")
                .contains(placeId);
    }

    @DisplayName("위시 리스트 삭제 테스트")
    @Test
    void 위시리스트_삭제() throws Exception {

        Long placeId2 = 2L;

        Long memberId = test1.getMemberId();

        // given 장소 추가
        mockMvc.perform(post("/v1/places/" + placeId2 + "/wishlist")
                        .header("Authorization", "Bearer " + accessToken) // 헤더에 Authorization 값을 추가
                        .param("memberId", String.valueOf(memberId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        // when 장소 삭제
        mockMvc.perform(delete("/v1/places/" + placeId2 + "/wishlist")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("memberId", String.valueOf(memberId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        // then 확인
        mockMvc.perform(get("/v1/members/" + memberId + "/wishlist")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(1))
                .andDo(print());
    }

}