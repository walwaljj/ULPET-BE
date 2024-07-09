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

    // 카테고리
    CATEGORY_DOSE_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),

    // 입력 및 응답
    API_CALL_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 응답 형식입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다."),

    // 인증
    CERTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "인증 정보를 찾을 수 없습니다."),
    MEMBER_CERTIFICATION_LIST_IS_EMPTY(HttpStatus.NOT_FOUND, "멤버의 인증 정보가 비어있습니다."),
    CERTIFICATION_BEFORE_MIDNIGHT(HttpStatus.CONFLICT, "장소에 대한 하루 인증 횟수를 초과하였습니다. 해당 장소는 자정 이후에 인증이 가능합니다."),
    NOT_AN_ALLOWED_AREA(HttpStatus.CONFLICT, "허용된 지역이 아닙니다.");

    private final HttpStatus status;
    private final String msg;

    ErrorCode(HttpStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }

}
