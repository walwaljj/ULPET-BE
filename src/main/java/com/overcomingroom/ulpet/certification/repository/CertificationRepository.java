package com.overcomingroom.ulpet.certification.repository;

import com.overcomingroom.ulpet.certification.domain.entity.Certification;
import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findAllByPlace(Place place);

    Optional<List<Certification>> findAllByMember(MemberEntity member);

    Optional<List<Certification>> findAllByPlaceAndMember(Place place, MemberEntity member);
}
