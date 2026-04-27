package com.fusion.psb.repository;

import com.fusion.psb.entity.ImageCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageCacheRepository extends JpaRepository<ImageCache, Long> {

    Optional<ImageCache> findByDescriptionHash(String descriptionHash);
}
