package com.sparta.mini_project01.service;

import com.sparta.mini_project01.controller.request.CommentRequestDto;
import com.sparta.mini_project01.controller.response.ResponseDto;
import com.sparta.mini_project01.controller.response.SubCommentResponseDto;
import com.sparta.mini_project01.domain.*;
import com.sparta.mini_project01.domain.SubComment;
import com.sparta.mini_project01.jwt.TokenProvider;
import com.sparta.mini_project01.repository.SubCommentHeartRepository;
import com.sparta.mini_project01.repository.SubCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubCommentService {

  private final SubCommentRepository subCommentRepository;

  private final TokenProvider tokenProvider;
  private final CommentService commentService;
  private final SubCommentHeartRepository heartRepository;

  @Transactional
  public ResponseDto<?> createSubComment(CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Comment comment = commentService.isPresentComment(requestDto.getRequestId());
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    SubComment subComment = SubComment.builder()
        .member(member)
        .comment(comment)
        .content(requestDto.getContent())
        .build();
    subCommentRepository.save(subComment);
    return ResponseDto.success(
        SubCommentResponseDto.builder()
            .id(subComment.getId())
            .author(subComment.getMember().getNickname())
            .content(subComment.getContent())
            .likes(subComment.getLikes())
            .createdAt(subComment.getCreatedAt())
            .modifiedAt(subComment.getModifiedAt())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllSubCommentsByComment(Long subcommentId) {
    Comment comment = commentService.isPresentComment(subcommentId);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
    }

    List<SubComment> subCommentList = subCommentRepository.findAllByComment(comment);
    List<SubCommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (SubComment subComment : subCommentList) {
      commentResponseDtoList.add(
          SubCommentResponseDto.builder()
              .id(subComment.getId())
              .author(subComment.getMember().getNickname())
              .content(subComment.getContent())
              .likes(subComment.getLikes())
              .createdAt(subComment.getCreatedAt())
              .modifiedAt(subComment.getModifiedAt())
              .build()
      );
    }
    return ResponseDto.success(commentResponseDtoList);
  }

  @Transactional
  public ResponseDto<?> updateSubComment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Comment comment = commentService.isPresentComment(requestDto.getRequestId());
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
    }

    SubComment subComment = isPresentSubComment(id);
    if (null == subComment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글 id 입니다.");
    }

    if (subComment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    subComment.update(requestDto);
    return ResponseDto.success(
        SubCommentResponseDto.builder()
            .id(subComment.getId())
            .author(subComment.getMember().getNickname())
            .content(subComment.getContent())
            .likes(subComment.getLikes())
            .createdAt(subComment.getCreatedAt())
            .modifiedAt(subComment.getModifiedAt())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> deleteSubComment(Long id, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    SubComment subComment = isPresentSubComment(id);
    if (null == subComment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글 id 입니다.");
    }

    if (subComment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    subCommentRepository.delete(subComment);
    return ResponseDto.success("success");
  }

  @Transactional(readOnly = true)
  public SubComment isPresentSubComment(Long id) {
    Optional<SubComment> optionalSubComment = subCommentRepository.findById(id);
    return optionalSubComment.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }


  public SubCommentHeart isPresentHeart(Long subCommentId, String nickname) {
    Optional<SubCommentHeart> optionalHeart = heartRepository.findByRequestIdAndNickname(subCommentId,nickname);
    return optionalHeart.orElse(null);
  }

  @Transactional
  public ResponseDto<?> likeSubComment(Long id, HttpServletRequest request) {

    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    SubComment subComment = isPresentSubComment(id);
    if (null == subComment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글 id 입니다.");
    }

    SubCommentHeart subCommentHeart = isPresentHeart(subComment.getId(), member.getNickname());
    if(null == subCommentHeart)
      heartRepository.save(SubCommentHeart.builder().requestId(subComment.getId()).nickname(member.getNickname()).build());
    else
      heartRepository.delete(subCommentHeart);

    subComment.updateLikes(heartRepository.findAllByRequestId(subComment.getId()).size());

    return ResponseDto.success("like success");
  }

}
