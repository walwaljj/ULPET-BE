package com.overcomingroom.ulpet.place.repository;

import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.repository.support.Querydsl4RepositorySupport;

public class PlaceRepositoryImp extends Querydsl4RepositorySupport implements PlaceRepositoryCustom {
    public PlaceRepositoryImp() {
        super(Place.class);
    }
}
