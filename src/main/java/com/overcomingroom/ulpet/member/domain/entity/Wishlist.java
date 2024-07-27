package com.overcomingroom.ulpet.member.domain.entity;

import com.overcomingroom.ulpet.place.domain.entity.Place;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {

    @Id
    private Long memberId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @Column(name = "place_id")
    private List<Place> place = new ArrayList<>();

}
