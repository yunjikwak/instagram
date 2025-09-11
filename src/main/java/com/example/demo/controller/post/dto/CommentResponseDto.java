package com.example.demo.controller.post.dto;

import com.example.demo.controller.user.dto.UserSimpleResponseDto;
import com.example.demo.repository.post.entity.Comment;
import com.example.demo.repository.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponseDto {
    private Integer id;
    private String content;
    private Comment.CommentStatus commentStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String authorName;
    private List<CommentResponseDto> children;

    public static CommentResponseDto from(Comment entity) {
        return new CommentResponseDto(
                entity.getId(),
                entity.getContent(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUser().getName(),
                entity.getChildren().stream()
                        .map(CommentResponseDto::from)
                        .toList()
        );
    }
}
