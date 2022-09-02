package com.sparta.mini_project01.repository;

import com.sparta.mini_project01.domain.SubCommentHeart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface SubCommentHeartRepository extends JpaRepository<SubCommentHeart, Long> {
  Optional<SubCommentHeart> findByRequestIdAndNickname(Long SubCommentId, String Nickname);
  List<SubCommentHeart> findAllByRequestId(Long RequestId);
}
