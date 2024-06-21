package com.overcomingroom.ulpet.place.repository;

import com.overcomingroom.ulpet.place.domain.entity.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {
    PlaceImage findByPlaceId(Long placeId);
}
