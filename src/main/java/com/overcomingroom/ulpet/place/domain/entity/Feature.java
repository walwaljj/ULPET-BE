package com.overcomingroom.ulpet.place.domain.entity;

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
public class Feature {

    @Id
    @Column(unique = true)
    private String feature;

    @ManyToMany(mappedBy = "features")
    private List<Place> places = new ArrayList<>();

}
