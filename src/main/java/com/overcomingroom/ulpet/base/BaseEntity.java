package com.overcomingroom.ulpet.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseEntity {

    private LocalDateTime createdAt; // 생성 일

    private LocalDateTime updatedAt; // 업데이트 일

    @CreatedBy
    private Long createdBy; // 생성한 사용자의 ID
}
