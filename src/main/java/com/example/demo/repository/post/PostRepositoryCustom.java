package com.example.demo.repository.post;

import com.example.demo.controller.post.dto.PostWithLikeCountResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    PostWithLikeCountResponseDto findWithLikeCount(Integer postId);
    Page<PostWithLikeCountResponseDto> findAllWithLikeCount(Pageable pageable);

    Page<PostWithLikeCountResponseDto> findPostsByUserIdWithLikeCount(Integer myId, Pageable pageable);
}
