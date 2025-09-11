package com.example.demo.service;

import com.example.demo.controller.post.CommentCreateRequestDto;
import com.example.demo.controller.post.dto.CommentResponseDto;
import com.example.demo.controller.post.dto.CommentSimpleResponseDto;
import com.example.demo.repository.post.CommentRepository;
import com.example.demo.repository.post.PostRepository;
import com.example.demo.repository.post.entity.Comment;
import com.example.demo.repository.post.entity.Post;
import com.example.demo.repository.user.UserRepository;
import com.example.demo.repository.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public CommentResponseDto findById(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 comment Id"));
        return CommentResponseDto.from(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentSimpleResponseDto> findCommentsByPostId(Integer commentId, Pageable pageable) {
        return commentRepository.findCommentDtosByPostId(commentId, pageable);
    }

    @Transactional
    public CommentResponseDto create(Integer myId, Integer postId, @Valid CommentCreateRequestDto request) {
        // user 조회
        User user = userRepository.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));

        // post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));

        // 댓글 생성
        Comment comment = Comment.create(request.getContent(), post, user);
        Comment created = commentRepository.save(comment);
        return CommentResponseDto.from(created);
    }

    @Transactional
    public CommentResponseDto createChild(
            Integer myId, Integer postId, Integer parentId, CommentCreateRequestDto request
    ) {
        // user 조회
        User user = userRepository.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));

        // post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));

        // comment 조회
        Comment comment = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));

        Comment child = Comment.createChild(request.getContent(), post, user, comment);
        comment.addChildComment(child);

        Comment saved = commentRepository.save(child);
        return CommentResponseDto.from(saved);

    }

    @Transactional
    public CommentResponseDto update(Integer myId, Integer commentId, CommentCreateRequestDto request) {
        // comment 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));

        // 본인인지 확인
        if (!comment.getUser().getId().equals(myId)) {
            throw new SecurityException("댓글 수정 권한 없음");
        }

        comment.update(request.getContent());
        return CommentResponseDto.from(comment);

    }

    @Transactional
    public void delete(Integer myId, Integer commentId) {
        // comment 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));

        // 본인인지 확인
        if (!comment.getUser().getId().equals(myId)) {
            throw new SecurityException("댓글 수정 권한 없음");
        }

        // 활성화된 상태인지 확인
        if (comment.getStatus() == Comment.CommentStatus.DELETED) {
            throw new IllegalArgumentException("이미 삭제된 게시글입니다.");
        }
        comment.changeToDelete();
    }
}
