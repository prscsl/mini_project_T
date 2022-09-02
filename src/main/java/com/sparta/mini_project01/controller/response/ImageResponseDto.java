package com.sparta.mini_project01.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ImageResponseDto {
    private String imageUrl;

    public ImageResponseDto(String imageUrl){
        this.imageUrl = imageUrl;

    }
}
