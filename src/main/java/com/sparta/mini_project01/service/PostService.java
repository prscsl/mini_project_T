package com.sparta.mini_project01.service;

import com.sparta.mini_project01.controller.request.PostRequestDto;
import com.sparta.mini_project01.controller.response.*;
import com.sparta.mini_project01.domain.*;
import com.sparta.mini_project01.jwt.TokenProvider;
import com.sparta.mini_project01.repository.CommentRepository;
import com.sparta.mini_project01.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final S3UploaderService s3Uploader;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request, MultipartFile multipartFile) throws IOException {
    //토큰 있는지 확인
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    // 토큰통해서 해당 멤버 불러오기
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    //이미지 s3업로드(s3UploaderService 메서드 활용) 후 값 image변수에 입력
    Image image = s3Uploader.uploadFiles(multipartFile,"static/");

    //저장할 post 변수에 해당 값 입력 후 postRepository에 저장
    Post post = Post.builder()
        .title(requestDto.getTitle())
        .placetitle(requestDto.getPlacetitle())
        .content(requestDto.getContent())
        .imgUrl(image.getPath())
        .imageKey(image.getKey())
        .member(member)
        .build();
    postRepository.save(post);

    // 저장 완료후 responseDto 통해 전달
    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .imageUrl(post.getImgUrl())
            .placeTitle(post.getPlacetitle())
            .title(post.getTitle())
            .content(post.getContent())
            .author(post.getMember().getNickname())
            .createdAt(post.getCreatedAt())
            .build()
    );
  }

  //게시글 객체 불러오기
  @Transactional(readOnly = true)
  public ResponseDto<?> getPost(Long id) {
    //받아온 id값으로 불러올 post find
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    //게시글 객체 불러오기에서 따라올, 해당 포스트 댓글들 post정보로 find, List 형태로 생성
    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    //List 내용 순차적으로 comment에 대입하고 commentResponseDto형식으로 commentResponseDtoList에 하나씩 주입
    for (Comment comment : commentList) {
      commentResponseDtoList.add(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getNickname())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      );
    }

    //클라이언트에서 필요한 정보들만 선별하여 response
    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .imageUrl(post.getImgUrl())
            .title(post.getTitle())
            .placeTitle(post.getPlacetitle())
            .author(post.getMember().getNickname())
            .content(post.getContent())
            .commentResponseDtoList(commentResponseDtoList)
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build()
    );
  }

  //게시글 전체불러오기
  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost() {
    //게시글 전체 저장소에서 불러와 List형태로 생성
    List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();

    //List 순차적으로 주입할 비어있는 Dto List 생성
    List<PostResponseDto> postResponseDtoList = new ArrayList<>();

    //List 내용 순차적으로 post에 대입하고 postResponseDto형식으로 postResponseDtoList에 하나씩 주입
    for(Post post : postList){
      postResponseDtoList.add(
              PostResponseDto.builder()
                      .id(post.getId())
                      .imageUrl(post.getImgUrl())
                      .title(post.getTitle())
                      .placeTitle(post.getPlacetitle())
                      .author(post.getMember().getNickname())
                      .content(post.getContent())
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
    }
    return ResponseDto.success(postResponseDtoList);
  }

  @Transactional
  public ResponseDto<Post> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request,
                                      MultipartFile multipartFile) throws IOException {
    //토큰 있는지 확인
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    //토큰 값에서 해당 멤버 불러오기
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    //id값으로 해당 post 불러오기
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }
    //member와 post member 동일한지 확인
    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    //이미지 업로드 및 해당 값 image 주입
    Image image = s3Uploader.uploadFiles(multipartFile,"static/");

    //기존 게시글에 연결되었던 image를 imagekey값을 통해 찾아서 제거
    s3Uploader.remove(post.getImageKey());

    //바뀐 내용 업데이트
    post.update(requestDto, image);
    return ResponseDto.success(post);
  }

  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
    //토큰 있는지 확인
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    //토큰 통해 해당 멤버 불러오기
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    //id로 해당 post 불러오기
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }
    //post의 member와 토큰의 member가 동일한지 확인
    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }

    //imagekey값으로 해당 image 찾아서 삭제
    s3Uploader.remove(post.getImageKey());

    //해당 게시글 삭제
    postRepository.delete(post);
    return ResponseDto.success("delete success");
  }

  //id로 해당 post 불러오기
  @Transactional(readOnly = true)
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  //헤더에 있는 토큰으로 해당 member 불러오기
  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }
}
