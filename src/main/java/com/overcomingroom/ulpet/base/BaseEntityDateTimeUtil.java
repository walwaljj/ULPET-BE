package com.overcomingroom.ulpet.base;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BaseEntityDateTimeUtil {

    /**
     * BaseEntity 의 속성에 맞게 시간을 변환합니다.
     *
     * @param apiDateTime api에서 가져온 날짜
     * @param datetimePattern 변환할 대상의 패턴
     * @return LocalDateTime
     */
    public static LocalDateTime localDateTimeParse(String apiDateTime, String datetimePattern){
        // DateTimeFormatter를 사용하여 문자열의 포맷 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datetimePattern);

        return LocalDateTime.parse(apiDateTime, formatter);
    }

}
