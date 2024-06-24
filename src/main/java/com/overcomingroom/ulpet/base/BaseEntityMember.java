package com.overcomingroom.ulpet.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityMember {

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private LocalDateTime createdAt; // 생성 일

  @LastModifiedDate
  private LocalDateTime updatedAt; // 업데이트 일

  private LocalDateTime deletedAt; // 삭제 일
}