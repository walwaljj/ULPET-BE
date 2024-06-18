package com.overcomingroom.ulpet.base;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BaseEntityDateTimeUtil {

    /**
     * BaseEntity 의 속성에 맞게 시간을 변환합니다.
     *
     * @param apiDateTime     api에서 가져온 날짜
     * @param datetimePattern 변환할 대상의 패턴
     * @return LocalDateTime
     */
    public static LocalDateTime localDateTimeToLocalDateTimeParse(String apiDateTime, String datetimePattern) {
        // DateTimeFormatter를 사용하여 문자열의 포맷 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datetimePattern);

        return LocalDateTime.parse(apiDateTime, formatter);
    }

    /**
     * BaseEntity 의 속성에 맞게 시간을 변환합니다.
     *
     * @param apiDateTime     api에서 가져온 날짜
     * @param datetimePattern 변환할 대상의 패턴
     * @return LocalDateTime
     */
    public static LocalDateTime localDateToLocalDateTimeParse(String apiDateTime, String datetimePattern) {

        // DateTimeFormatter를 사용하여 문자열의 포맷 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datetimePattern);
        // LocalDate로 변환
        LocalDate localDate = LocalDate.parse(apiDateTime, formatter);

        // LocalDateTime 변환, 00시 00분 00초
        return localDate.atStartOfDay();
    }

}
