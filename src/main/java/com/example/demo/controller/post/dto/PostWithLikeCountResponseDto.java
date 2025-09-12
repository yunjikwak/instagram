package com.example.demo.controller.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostWithLikeCountResponseDto {
    private Integer postId;
    private String content;
    private String authorName;
    private Long likeCount;

    public static PostWithLikeCountResponseDto from(Integer postId, String content, String authorName, Long likeCount) {
        return new PostWithLikeCountResponseDto(
                postId,
                content,
                authorName,
                likeCount
        );
    }
}
