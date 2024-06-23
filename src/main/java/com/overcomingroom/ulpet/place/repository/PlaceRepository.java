package com.overcomingroom.ulpet.place.repository;

import com.overcomingroom.ulpet.place.domain.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {
    Optional<Place> findByContentId(long contentId);

    Optional<Place> findByPlaceNameAndAddress(String placeName, String address);

    List<Place> findAllByOrderByCreatedAtDesc();

}
