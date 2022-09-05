package com.sparta.mini_project01.controller;

import com.sparta.mini_project01.controller.request.CommentRequestDto;
import com.sparta.mini_project01.controller.response.ResponseDto;
import com.sparta.mini_project01.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Validated
@RequiredArgsConstructor
@RestController
public class CommentController {

  private final CommentService commentService;

  @RequestMapping(value = "/api/auth/comment/{postId}", method = RequestMethod.POST)
  public ResponseDto<?> createComment(@PathVariable("postId") Long id, @RequestBody CommentRequestDto requestDto,
      HttpServletRequest request) {
    return commentService.createComment(id, requestDto, request);
  }

  @RequestMapping(value = "/api/comment/{id}", method = RequestMethod.GET)
  public ResponseDto<?> getAllComments(@PathVariable Long id) {
    return commentService.getAllCommentsByPost(id);
  }

  @RequestMapping(value = "/api/auth/comment/{commentId}", method = RequestMethod.PUT)
  public ResponseDto<?> updateComment(@PathVariable("commentId") Long id, @RequestBody CommentRequestDto requestDto,
      HttpServletRequest request) {
    return commentService.updateComment(id, requestDto, request);
  }

  @RequestMapping(value = "/api/auth/comment/{commentId}", method = RequestMethod.DELETE)
  public ResponseDto<?> deleteComment(@PathVariable("commentId") Long id,
      HttpServletRequest request) {
    return commentService.deleteComment(id, request);
  }
}
