package com.sparta.mini_project01.domain;

import com.sparta.mini_project01.controller.request.PostRequestDto;
import com.sparta.mini_project01.controller.response.ImageResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

<<<<<<< HEAD
=======

  @Column(nullable = false)
  private String placetitle;

>>>>>>> 0d3325955d3ec2026bf9816c4bae1809a7967658
  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private String imgUrl;

  @Column(nullable = false)
  private String imageKey;

  
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;


  public void update(PostRequestDto postRequestDto, Image image) {
    this.title = postRequestDto.getTitle();
    this.content = postRequestDto.getContent();
    this.imgUrl = image.getPath();
    this.imageKey = image.getKey();
  }

  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }

}
