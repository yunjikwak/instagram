package com.example.demo.controller.post.dto;

import com.example.demo.repository.post.entity.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentSimpleResponseDto {
    private Integer id;
    private String content;
    private Comment.CommentStatus commentStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String authorName;

    public static CommentSimpleResponseDto from(Comment entity) {
        return new CommentSimpleResponseDto(
                entity.getId(),
                entity.getContent(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUser().getName()
        );
    }

}
