package com.overcomingroom.ulpet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Member
    MEMBER_INVALID(HttpStatus.BAD_REQUEST, "멤버 정보가 유효하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "멤버를 찾을 수 없습니다."),
    MEMBER_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 회원입니다."),

    // Email
    EMAIL_SEND_FAILED(HttpStatus.BAD_REQUEST, "이메일 전송에 실패했습니다."),

    // OAuth
    LOGIN_ERROR(HttpStatus.BAD_REQUEST, "로그인 오류"),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다."),

    // 위치
    LOCATION_INFORMATION_NOT_FOUND(HttpStatus.NOT_FOUND, "위치 정보를 찾을 수 없습니다."),

    // 장소
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "장소 정보를 찾을 수 없습니다."),
    PLACE_ALREADY_SAVED_TO_WISHLIST(HttpStatus.BAD_REQUEST, "이미 위시리스트에 추가된 장소입니다."),
    PLACE_NOT_SAVED_IN_WISHLIST(HttpStatus.BAD_REQUEST, "위시리스트에 저장되지 않은 장소입니다."),


    // 입력 및 응답
    API_CALL_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 응답 형식입니다."),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다.");

    private final HttpStatus status;
    private final String msg;

    ErrorCode(HttpStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }

}
