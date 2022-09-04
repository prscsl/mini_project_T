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

  private final S3UploaderService s3Uploader;

  @RequestMapping(value = "/api/auth/place", method = RequestMethod.POST)
  public ResponseDto<?> createPost(@RequestPart(value = "key") PostRequestDto requestDto,
      HttpServletRequest request, @RequestPart(value = "file") MultipartFile multipartFile) throws IOException {
    return postService.createPost(requestDto, request, multipartFile);
  }

  @RequestMapping(value = "/api/place/{id}", method = RequestMethod.GET)
  public ResponseDto<?> getPost(@PathVariable Long id) {
    return postService.getPost(id);
  }

  @RequestMapping(value = "/api/place", method = RequestMethod.GET)
  public ResponseDto<?> getAllPosts() {
    return postService.getAllPost();
  }

  @RequestMapping(value = "/api/auth/place/{id}", method = RequestMethod.PUT)
  public ResponseDto<?> updatePost(@PathVariable Long id, @RequestPart(value = "key") PostRequestDto postRequestDto,
                                   @RequestPart(value = "file") MultipartFile multipartFile, HttpServletRequest request) throws IOException {
    return postService.updatePost(id, postRequestDto, request, multipartFile);
  }

  @RequestMapping(value = "/api/auth/place/{id}", method = RequestMethod.DELETE)
  public ResponseDto<?> deletePost(@PathVariable Long id,
      HttpServletRequest request) {
    return postService.deletePost(id, request);
  }

  @RequestMapping(value = "/api/auth/place/{id}", method = RequestMethod.POST)
  public ResponseDto<?> likePost(@PathVariable Long id, HttpServletRequest request) {
    return postService.likePost(id, request);
  }
}
