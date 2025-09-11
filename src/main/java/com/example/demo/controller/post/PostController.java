package com.example.demo.controller.post;

import com.example.demo.controller.post.dto.*;
import com.example.demo.repository.user.entity.User;
import com.example.demo.service.CommentService;
import com.example.demo.service.LikeService;
import com.example.demo.service.PostService;
import com.example.demo.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final ReportService reportService;

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

    // create
    @PostMapping("")
    public ResponseEntity<PostResponseDto> create(
            @AuthenticationPrincipal User loggedInUser,
            @RequestPart("request") PostCreateRequestDto request,
            @RequestPart("files") List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("첨부파일 한 개 이상 필요");
        }
        if (files.size() > 10) {
            throw new IllegalArgumentException("파일이 10개 초과");
        }

        Integer myId = loggedInUser.getId();
        PostResponseDto post = postService.create(myId, request, files);
        return ResponseEntity.ok(post);
    }

    // update
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> update(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable Integer id,
            @RequestBody PostCreateRequestDto request
    ) {
        Integer myId = loggedInUser.getId();

        PostResponseDto updatedPost = postService.update(myId, id, request);
        return ResponseEntity.ok(updatedPost);
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable Integer id
    ) {
        Integer myId = loggedInUser.getId();

        postService.delete(myId, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // like
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> like(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable Integer id
    ) {
        // 사용자 인증 정보 꺼내기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User principal = (User) authentication.getPrincipal();
//        Integer myId = principal.getId();
        Integer myId = loggedInUser.getId();

        likeService.like(myId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlike(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable Integer id) {
        // 사용자 인증 정보 꺼내기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User principal = (User) authentication.getPrincipal();
//        Integer myId = principal.getId();
        Integer myId = loggedInUser.getId();

        likeService.unlike(myId, id);
        return ResponseEntity.ok().build();
    }

    // comment
    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<CommentSimpleResponseDto>> comment(
            @PathVariable Integer id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("잘못된 페이지네이션 값입니다.");
        }

        Page<CommentSimpleResponseDto> comments = commentService.findCommentsByPostId(id, pageable);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable Integer id,
            @RequestBody @Valid CommentCreateRequestDto request
    ) {
        Integer myId = loggedInUser.getId();
        CommentResponseDto comment = commentService.create(myId, id, request);
        return ResponseEntity.ok(comment);
    }

    // report
    @PostMapping("/{id}/report")
    public ResponseEntity<Void> report(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable Integer id,
            @RequestBody @Valid ReportCreateRequestDto request
    ) {
        Integer myId = loggedInUser.getId();
        reportService.create(myId, id, request);
        return ResponseEntity.ok().build();
    }
}