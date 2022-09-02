package com.sparta.mini_project01.repository;

import com.sparta.mini_project01.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image,Long> {

    Optional<Image> findByImgURL(String imgUrl);
}
