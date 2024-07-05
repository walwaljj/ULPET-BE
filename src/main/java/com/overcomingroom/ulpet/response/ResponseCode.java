package com.overcomingroom.ulpet.response;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@ToString
public enum ResponseCode {
    // Member
    MEMBER_INFO_GET_SUCCESSFUL(HttpStatus.OK, "200", "멤버 정보 조회 성공"),
    MEMBER_SIGN_UP_SUCCESS(HttpStatus.OK, "200", "회원가입 성공"),
    MEMBER_WITHDRAWAL_SUCCESS(HttpStatus.OK, "200", "회원탈퇴 성공"),
    MEMBER_UPDATE_SUCCESS(HttpStatus.OK,"200", "회원정보 수정 성공"),
    MEMBER_EMAIL_CHECK_SUCCESS(HttpStatus.OK, "200", "회원 이메일 조회 성공"),

    // Email
    EMAIL_SEND_SUCCESS(HttpStatus.OK, "200", "이메일 전송 성공"),

    // Auth
    TOKEN_REISSUE_SUCCESS(HttpStatus.OK, "200", "토큰 재발급 성공"),

    // OAuth,
    LOGIN_SUCCESSFUL(HttpStatus.OK, "200", "로그인 성공"),

    // TOUR API
    TOUR_API_CALL_SUCCESSFUL(HttpStatus.OK, "200", "tour api 호출 성공"),

    // Place
    PLACE_SEARCH(HttpStatus.OK, "200", "장소 통합 검색 성공"),
    NEW_PLACE_LIST_SUCCESSFULLY_VIEWED(HttpStatus.OK, "200", "새로운 장소 목록 조회 성공"),
    PLACE_DETAIL(HttpStatus.OK, "200", "장소 상세 조회 성공"),
    PLACE_ADDED_TO_WISHLIST(HttpStatus.OK, "200", "위시리스트에 장소 추가 완료"),
    WISHLIST_READ_SUCCESSFUL(HttpStatus.OK, "200", "위시리스트 읽기 성공"),
    PLACE_REMOVED_FROM_WISHLIST(HttpStatus.OK, "200", "위시리스트에서 장소가 삭제됨");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ResponseCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public ResponseEntity<ResResult> toResponse(Object data) {
        return new ResponseEntity<>(ResResult.builder()
                .responseCode(this)
                .code(this.code)
                .message(this.message)
                .data(data)
                .build(), httpStatus.OK);
    }
}
