package com.sparta.mini_project01.repository;

import com.sparta.mini_project01.domain.PostHeart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PostHeartRepository extends JpaRepository< PostHeart, Long> {
  Optional< PostHeart> findByRequestIdAndNickname(Long PostId, String Nickname);
  List< PostHeart> findAllByRequestId(Long RequestId);

  List<PostHeart> findAllByNickname(String Nickname);
}
