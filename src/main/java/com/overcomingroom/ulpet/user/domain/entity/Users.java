package com.overcomingroom.ulpet.user.domain.entity;

import com.overcomingroom.ulpet.base.BaseEntity;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String profile_image;

    private Float familiarity;

    @ManyToMany
    private List<Place> wishList = new ArrayList<>();


}
