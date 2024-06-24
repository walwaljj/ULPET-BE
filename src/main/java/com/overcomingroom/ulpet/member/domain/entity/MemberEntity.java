package com.overcomingroom.ulpet.member.domain.entity;

import com.overcomingroom.ulpet.base.BaseEntityMember;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Setter
@Getter
@Table(name = "member")
@SQLDelete(sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP WHERE member_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class MemberEntity extends BaseEntityMember implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String nickname;

  @Column(nullable = false)
  private String profileImage;

  @Column(nullable = false)
  private Float familiarity = 0.0f;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public static MemberEntity of(String username, String password, String nickname, String profileImage) {
    var member = new MemberEntity();
    member.setUsername(username);
    member.setPassword(password);
    member.setNickname(nickname);
    member.setProfileImage(profileImage);
    return member;
  }

  public static MemberEntity of(Long memberId, String username, String password, String nickname, String profileImage, Float familiarity) {
    var member = new MemberEntity();
    member.setMemberId(memberId);
    member.setUsername(username);
    member.setPassword(password);
    member.setNickname(nickname);
    member.setProfileImage(profileImage);
    member.setFamiliarity(familiarity);
    return member;
  }

}
