package com.sparta.mini_project01.domain;

import com.sparta.mini_project01.Validator.URLvalidator;
import com.sparta.mini_project01.Validator.ValidateObject;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "postImage")
public class Image extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String imgURL;

    public Image(Post post, String imageUrl){
        URLvalidator.isValidURL(imageUrl);
        ValidateObject.postValidate(post);
        this.post=post;
        this.imgURL = imageUrl;
    }
}
