package com.sparta.mini_project01.repository;

import com.sparta.mini_project01.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();
  List<Post> findAll();

  List<Post> findAllByMemberId(Long id);

}
