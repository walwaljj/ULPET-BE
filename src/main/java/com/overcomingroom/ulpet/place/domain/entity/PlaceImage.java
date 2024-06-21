package com.overcomingroom.ulpet.place.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    public void modifyPlaceImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
