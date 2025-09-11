package com.example.demo.repository.post;

import com.example.demo.controller.post.dto.CommentSimpleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    Page<CommentSimpleResponseDto> findCommentDtosByPostId (Integer postId, Pageable pageable);
}
