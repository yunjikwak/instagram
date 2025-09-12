package com.example.demo.service;

import com.example.demo.common.exception.BaseException;
import com.example.demo.common.response.BaseResponseStatus;
import com.example.demo.controller.post.dto.CommentCreateRequestDto;
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
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_COMMENT));

        if (comment.getStatus() == Comment.CommentStatus.DELETED) {
            throw new BaseException(BaseResponseStatus.COMMENT_CONFLICT);
        }

        return CommentResponseDto.from(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentSimpleResponseDto> findCommentsByPostId(Integer postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new BaseException(BaseResponseStatus.NOT_FIND_POST);
        }
        return commentRepository.findCommentDtosByPostId(postId, pageable);
    }

    @Transactional
    public CommentResponseDto create(Integer myId, Integer postId, @Valid CommentCreateRequestDto request) {
        // user 조회
        User user = userRepository.findById(myId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_USER));

        // post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_POST));

        // 삭제된 게시물인지 확인
        if (post.getStatus() == Post.PostStatus.DELETED) {
            throw new BaseException(BaseResponseStatus.POST_CONFLICT);
        }

        // 댓글 생성
        Comment comment = Comment.create(request.getContent(), post, user);
        Comment created = commentRepository.save(comment);
        return CommentResponseDto.from(created);
    }

    @Transactional
    public CommentResponseDto createChild(
            Integer myId, Integer parentId, CommentCreateRequestDto request
    ) {
        // user 조회
        User user = userRepository.findById(myId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_USER));

        // comment 조회
        Comment comment = commentRepository.findById(parentId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_COMMENT));

        // 삭제된 댓글인지 확인
        if (comment.getStatus() == Comment.CommentStatus.DELETED) {
            throw new BaseException(BaseResponseStatus.COMMENT_CONFLICT);
        }

        Post post = comment.getPost();

        Comment child = Comment.createChild(request.getContent(), post, user, comment);
        comment.addChildComment(child);

        Comment saved = commentRepository.save(child);
        return CommentResponseDto.from(saved);

    }

    @Transactional
    public CommentResponseDto update(Integer myId, Integer commentId, CommentCreateRequestDto request) {
        // comment 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_COMMENT));

        // 본인인지 확인
        if (!comment.getUser().getId().equals(myId)) {
            throw new BaseException(BaseResponseStatus.FORBIDDEN_ACCESS);
        }

        comment.update(request.getContent());
        return CommentResponseDto.from(comment);

    }

    @Transactional
    public void delete(Integer myId, Integer commentId) {
        // comment 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_COMMENT));

        // 본인인지 확인
        if (!comment.getUser().getId().equals(myId)) {
            throw new BaseException(BaseResponseStatus.FORBIDDEN_ACCESS);
        }

        // 활성화된 상태인지 확인
        if (comment.getStatus() == Comment.CommentStatus.DELETED) {
            throw new BaseException(BaseResponseStatus.COMMENT_CONFLICT);
        }
        comment.changeToDelete();
    }
}
