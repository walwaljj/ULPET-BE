package com.overcomingroom.ulpet.member.domain.entity;

public record Member(
    Long memberId,
    String username,
    String password,
    String nickname,
    String profileImage,
    Float familiarity
) {
  public static Member from(MemberEntity memberEntity) {
    return new Member(
        memberEntity.getMemberId(),
        memberEntity.getUsername(),
        memberEntity.getPassword(),
        memberEntity.getNickname(),
        memberEntity.getProfileImage(),
        memberEntity.getFamiliarity()
        );
  }
}
