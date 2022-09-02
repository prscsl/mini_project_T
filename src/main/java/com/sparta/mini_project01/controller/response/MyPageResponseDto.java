package com.sparta.mini_project01.controller.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDto {
    private Long id;
    private List<PostResponseDto> postResponseDtoList;
    private List<CommentResponseDto> commentResponseDtoList;
    private List<SubCommentResponseDto> subCommentResponseDtoList;
    private List<PostResponseDto> heartPostResponseDtoList;
    private List<CommentResponseDto> heartCommentResponseDto;

}
