package com.overcomingroom.ulpet.member.repository;

import com.overcomingroom.ulpet.member.domain.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
}
