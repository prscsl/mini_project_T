package com.sparta.mini_project01.domain;

import com.sparta.mini_project01.controller.request.CommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SubComment extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @JoinColumn(name = "comment_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Comment comment;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private int likes;


  public void update(CommentRequestDto commentRequestDto) {
    this.content = commentRequestDto.getContent();
  }

  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }

  public void updateLikes(int num){
    this.likes = num;
  }
}
