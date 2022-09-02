package com.sparta.mini_project01.repository;

import com.sparta.mini_project01.domain.Comment;
import com.sparta.mini_project01.domain.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
  List<SubComment> findAllByComment(Comment comment);
  void deleteAllByComment(Comment comment);

  List<SubComment> findAllByMemberId(Long id);
}
