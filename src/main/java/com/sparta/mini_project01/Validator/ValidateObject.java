package com.sparta.mini_project01.Validator;

import com.sparta.mini_project01.domain.Post;

public class  ValidateObject  {


    public static void  postValidate(Post post){
        if(post.getId()==null || post.getId()<=0){
            throw  new IllegalArgumentException("유효하지 않는 Post Id입니다.");
        }
    }
}
