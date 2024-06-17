package com.overcomingroom.ulpet.place.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Builder
public class PlaceImage {

    @Id
    private Long placeId;

    private String imageUrl;
}
