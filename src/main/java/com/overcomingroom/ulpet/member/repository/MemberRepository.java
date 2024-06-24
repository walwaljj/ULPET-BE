package com.overcomingroom.ulpet.member.repository;

import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  Optional<MemberEntity> findByUsername(String username);
}

