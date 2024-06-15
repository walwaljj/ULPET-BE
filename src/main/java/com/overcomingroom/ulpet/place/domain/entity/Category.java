package com.overcomingroom.ulpet.place.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentTypeId;

    private String categoryName;

    private String subcategory;

    public Category(Long contentTypeId, String categoryName, String subcategory) {
        this.contentTypeId = contentTypeId;
        this.categoryName = categoryName;
        this.subcategory = subcategory;
    }
}
