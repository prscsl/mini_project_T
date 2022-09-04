package com.sparta.mini_project01.domain;

import com.sparta.mini_project01.Validator.URLvalidator;
import com.sparta.mini_project01.Validator.ValidateObject;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
public class Image extends Timestamped {

    private String key;
    private String path;


    @Builder
    public Image(String key, String path) {
        this.key = key;
        this.path = path;
    }


//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name="post_id", nullable = false)
//    private Post post;
//
//    @Column(nullable = false)
//    private String imgURL;

//    public Image(Post post, String imageUrl){
//        URLvalidator.isValidURL(imageUrl);
//        ValidateObject.postValidate(post);
//        this.post=post;
//        this.imgURL = imageUrl;
//    }
}
