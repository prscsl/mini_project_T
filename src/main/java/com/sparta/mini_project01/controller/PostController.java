package com.sparta.mini_project01.controller;

import com.sparta.mini_project01.controller.request.PostRequestDto;
import com.sparta.mini_project01.controller.response.ImageResponseDto;
import com.sparta.mini_project01.controller.response.ResponseDto;
import com.sparta.mini_project01.domain.Image;
import com.sparta.mini_project01.service.PostService;
import com.sparta.mini_project01.service.S3UploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class PostController {

  private final PostService postService;

  //맛집 리뷰 게시글 작성&이미지업로드 동시 진행
  @RequestMapping(value = "/api/auth/place", method = RequestMethod.POST)
  public ResponseDto<?> createPost(@RequestPart(value = "request") PostRequestDto requestDto,
      HttpServletRequest request, @RequestPart(value = "image") MultipartFile multipartFile) throws IOException {
    return postService.createPost(requestDto, request, multipartFile);
  }

  //게시글 객체 불러오기, 해당 댓글까지 출력
  @RequestMapping(value = "/api/place/{id}", method = RequestMethod.GET)
  public ResponseDto<?> getPost(@PathVariable Long id) {
    return postService.getPost(id);
  }

  //전체 게시글 목록 불러오기
  @RequestMapping(value = "/api/place", method = RequestMethod.GET)
  public ResponseDto<?> getAllPosts() {
    return postService.getAllPost();
  }

  //맛집 리뷰 게시글 &이미지 수정
  @RequestMapping(value = "/api/auth/place/{id}", method = RequestMethod.PUT)
  public ResponseDto<?> updatePost(@PathVariable Long id, @RequestPart(value = "request") PostRequestDto postRequestDto,
                                   @RequestPart(value = "image") MultipartFile multipartFile, HttpServletRequest request) throws IOException {
    return postService.updatePost(id, postRequestDto, request, multipartFile);
  }

  //맛집 리뷰 게시글 &이미지 삭제
  @RequestMapping(value = "/api/auth/place/{id}", method = RequestMethod.DELETE)
  public ResponseDto<?> deletePost(@PathVariable Long id,
      HttpServletRequest request) {
    return postService.deletePost(id, request);
  }
}
