package com.example.demo.controller.post;

import com.example.demo.controller.post.dto.PostCreateRequestDto;
import com.example.demo.controller.post.dto.PostResponseDto;
import com.example.demo.controller.post.dto.PostWithLikeCountResponseDto;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // read
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> post(@PathVariable Integer id) {
        PostResponseDto post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping("")
    public ResponseEntity<Page<PostWithLikeCountResponseDto>> posts(
            // 페이지네이션
                // 기본 - 10개씩, 최신순
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("잘못된 페이지네이션 값입니다.");
        }

        Page<PostWithLikeCountResponseDto> posts = postService.findAllPostsWithLikeCount(pageable);
        return ResponseEntity.ok(posts);
    }

    // write
    @PostMapping("")
    public ResponseEntity<PostResponseDto> create(@RequestBody PostCreateRequestDto request) {
        PostResponseDto post = postService.save(request);
        return ResponseEntity.ok(post);
    }

    // update
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> update(@PathVariable Integer id, @RequestBody PostCreateRequestDto request) {
        PostResponseDto updatedPost = postService.update(id, request);
        return ResponseEntity.ok(updatedPost);
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        postService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
