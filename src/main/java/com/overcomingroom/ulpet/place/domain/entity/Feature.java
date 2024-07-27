package com.overcomingroom.ulpet.place.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.overcomingroom.ulpet.certification.domain.entity.CertificationFeature;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    private String feature;

    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CertificationFeature> certificationFeatures = new ArrayList<>();

}
