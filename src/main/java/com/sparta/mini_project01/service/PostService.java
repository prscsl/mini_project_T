package com.sparta.mini_project01.service;

import com.sparta.mini_project01.controller.request.PostRequestDto;
import com.sparta.mini_project01.controller.response.*;
import com.sparta.mini_project01.domain.*;
import com.sparta.mini_project01.jwt.TokenProvider;
import com.sparta.mini_project01.repository.CommentRepository;
import com.sparta.mini_project01.repository.PostHeartRepository;
import com.sparta.mini_project01.repository.PostRepository;
import com.sparta.mini_project01.repository.SubCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
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
  private final PostHeartRepository heartRepository;
  private final CommentRepository commentRepository;
  private final SubCommentRepository subCommentRepository;
  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request, ImageResponseDto imageResponseDto) throws IOException {

//    ImageResponseDto imageResponseDto = new ImageResponseDto(s3Uploader.uploadFiles(multipartFile,"static/"));

//    if(multipartFile.isEmpty()){
//      return ResponseDto.fail("INVALID_FILE","파일이 유효하지 않습니다.");
//    }
//    try{
//     new ImageResponseDto(s3Uploader.uploadFiles(multipartFile,"static/"));
//    }catch (Exception e){
//      e.printStackTrace();
//      return ResponseDto.fail("INVALID_FILE","파일이 유효하지 않습니다.");
//    }

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

    Post post = Post.builder()
        .title(requestDto.getTitle())
        .placetitle(requestDto.getPlacetitle())
        .content(requestDto.getContent())
        .imgUrl(imageResponseDto.getImageUrl())
        .member(member)
        .build();
    postRepository.save(post);
    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .imageUrl(post.getImgUrl())
            .placeTitle(post.getPlacetitle())
            .title(post.getTitle())
            .content(post.getContent())
            .author(post.getMember().getNickname())
//            .likes(post.getLikes())
            .createdAt(post.getCreatedAt())
//            .modifiedAt(post.getModifiedAt())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getPost(Long id) {
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {

      List<SubComment> subCommentList = subCommentRepository.findAllByComment(comment);
      List<SubCommentResponseDto> subCommentResponseDtoList = new ArrayList<>();
      for (SubComment subComment : subCommentList) {
        subCommentResponseDtoList.add(
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

      commentResponseDtoList.add(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getNickname())
                      .content(comment.getContent())
                      .likes(comment.getLikes())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .SubCommentResponseDtoList(subCommentResponseDtoList)
                      .build()
      );

    }


    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .imageUrl(post.getImgUrl())
            .title(post.getTitle())
            .placeTitle(post.getPlacetitle())
            .author(post.getMember().getNickname())
            .content(post.getContent())
//            .likes(post.getLikes())
            .commentResponseDtoList(commentResponseDtoList)
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost() {
    List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
    List<PostResponseDto> postResponseDtoList = new ArrayList<>();

    for(Post post : postList){
      postResponseDtoList.add(
              PostResponseDto.builder()
                      .id(post.getId())
                      .imageUrl(post.getImgUrl())
                      .title(post.getTitle())
                      .placeTitle(post.getPlacetitle())
                      .author(post.getMember().getNickname())
                      .content(post.getContent())
//                      .likes(post.getLikes())
                      .createdAt(post.getCreatedAt())
//                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
    }
    return ResponseDto.success(postResponseDtoList);

//    return ResponseDto.success(postRepository.findAllByOrderByModifiedAtDesc());
  }




  @Transactional
  public ResponseDto<Post> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
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

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    post.update(requestDto);
    return ResponseDto.success(post);
  }

  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
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

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }

    postRepository.delete(post);
    return ResponseDto.success("delete success");
  }

  @Transactional(readOnly = true)
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

  public  PostHeart isPresentHeart(Long postId, String nickname) {
      Optional< PostHeart> optionalHeart = heartRepository.findByRequestIdAndNickname(postId,nickname);
      return optionalHeart.orElse(null);
  }

  @Transactional
  public ResponseDto<?> likePost(Long id, HttpServletRequest request) {

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

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    PostHeart heart = isPresentHeart(post.getId(), member.getNickname());

    if(null == heart)
      heartRepository.save(PostHeart.builder().requestId(post.getId()).nickname(member.getNickname()).build());
    else
      heartRepository.delete(heart);

    post.updateLikes(heartRepository.findAllByRequestId(post.getId()).size());

    return ResponseDto.success("like success");
  }

}
