package com.sparta.mini_project01.repository;

import com.sparta.mini_project01.domain.Comment;
import com.sparta.mini_project01.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByPost(Post post);
  void deleteAllByPost(Post post);
  List<Comment> findAllByMemberId(Long id);
}
