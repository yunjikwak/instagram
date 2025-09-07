package com.example.demo.controller.post.dto;

import com.example.demo.controller.user.dto.UserSimpleResponseDto;
import com.example.demo.repository.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponseDto {
    private Integer id;
    private String content;
    private Post.PostStatus postStatus;

    // 양식 존재하는지 확인하기
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private UserSimpleResponseDto user;

    public static PostResponseDto from(Post entity) {
        return new PostResponseDto(
                entity.getId(),
                entity.getContent(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                UserSimpleResponseDto.from(entity.getUser())
        );
    }

}
