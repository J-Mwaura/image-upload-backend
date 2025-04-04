package com.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.image.entity.Image;

import java.util.Optional;

/**
 * @author atjkm
 *
 */

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByName(String name);

}

