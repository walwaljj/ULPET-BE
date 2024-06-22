package com.overcomingroom.ulpet.place.controller;

import com.overcomingroom.ulpet.place.repository.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
class PlaceControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PlaceRepository placeRepository;

    @DisplayName("키워드 검색 테스트 - (장소명)")
    @Test
    void 장소명_검색() throws Exception {

        //when, then
        mockMvc.perform(get("/v1/search")
                        .param("keyword", "도솔암")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].address").value("울산광역시 북구 화동15길 77"))
                .andDo(print());
    }

    @DisplayName("키워드 검색 테스트 - (주소)")
    @Test
    void 주소_검색() throws Exception {

        //when, then
        mockMvc.perform(get("/v1/search")
                        .param("keyword", "울산광역시 동구 진성13길 92")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(1))
                .andDo(print());
    }

    @DisplayName("카테고리 검색 테스트")
    @Test
    void 카테고리_검색() throws Exception {

        //when, then
        mockMvc.perform(get("/v1/search")
                        .param("category", "SHOPPING")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].category").value("SHOPPING"))
                .andExpect(jsonPath("$.data[1].category").value("SHOPPING"))
                .andDo(print());
    }

    @DisplayName("검색 조건 생략 테스트 : 검색 조건이 없는 경우를 테스트 합니다. 전체 목록을 모두 반환해야 합니다.")
    @Test
    void 검색_조건_생략() throws Exception {

        int expectedSize = placeRepository.findAll().size();

        //when, then
        mockMvc.perform(get("/v1/search")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(expectedSize))
                .andDo(print());
    }

}