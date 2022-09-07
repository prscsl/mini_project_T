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

  //게시글 제목
  @Column(nullable = false)
  private String title;

  //게시글 리뷰할 대상 맛집 이름
  @Column(nullable = false)
  private String placetitle;

  //리뷰 내용
  @Column(nullable = false)
  private String content;

  //image 불러올 url
  @Column(nullable = false)
  private String imgUrl;

  //image삭제를 위한 key값
  @Column(nullable = false)
  private String imageKey;

  //post 해당 댓글 List
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  //post작성한 회원 정보
  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = FetchType.EAGER)
  private Member member;

  //수정시 활용할 메서드
  public void update(PostRequestDto postRequestDto, Image image) {
    this.title = postRequestDto.getTitle();
    this.content = postRequestDto.getContent();
    this.placetitle = postRequestDto.getPlacetitle();
    this.imgUrl = image.getPath();
    this.imageKey = image.getKey();
  }

  //post의 member와 주입된 member 동일 확인 메서드
  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }

}
