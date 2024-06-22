package com.overcomingroom.ulpet.place.repository;

import com.overcomingroom.ulpet.place.domain.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<Feature, String> {
}
