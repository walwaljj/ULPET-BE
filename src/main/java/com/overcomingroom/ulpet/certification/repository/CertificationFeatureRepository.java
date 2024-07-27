package com.overcomingroom.ulpet.certification.repository;

import com.overcomingroom.ulpet.certification.domain.entity.Certification;
import com.overcomingroom.ulpet.certification.domain.entity.CertificationFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificationFeatureRepository extends JpaRepository<CertificationFeature, Long> {
    Optional<List<CertificationFeature>> findByCertification(Certification certification);
}
