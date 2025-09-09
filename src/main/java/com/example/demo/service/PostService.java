package com.example.demo.service;

import com.example.demo.controller.post.dto.PostCreateRequestDto;
import com.example.demo.controller.post.dto.PostResponseDto;
import com.example.demo.controller.post.dto.PostWithLikeCountResponseDto;
import com.example.demo.repository.post.PostRepository;
import com.example.demo.repository.post.entity.Attachment;
import com.example.demo.repository.post.entity.Post;
import com.example.demo.repository.user.UserRepository;
import com.example.demo.repository.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;

    @Transactional(readOnly = true)
    public PostResponseDto findById(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));
        return PostResponseDto.from(post);
    }

    @Transactional(readOnly = true)
    public Page<PostWithLikeCountResponseDto> findAllPostsWithLikeCount(Pageable pageable) {
        return postRepository.findAllWithLikeCount(pageable);
    }

    @Transactional
    public PostResponseDto save(PostCreateRequestDto request, List<MultipartFile> files) {
        // 작성자 조회
        Integer userId = request.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));

        // 작성한 post 저장
            // 이미지 추가하기
        Post post = Post.create(
                request.getContent(),
                user
        );

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            // 파일 업로드
            String filePath = attachmentService.uploadFile(file);

            // 임시 이미지 고정
            Attachment.AttachmentType type = Attachment.AttachmentType.IMAGE;

            // attachment 엔티티 생성
            Attachment attachment = Attachment.create(
                    type,
                    filePath,
                    i+1,
                    post
            );

            // post에도 추가하기
            post.addAttachment(attachment);
        }

        Post created = postRepository.save(post);
        return PostResponseDto.from(created);
    }

    @Transactional
    public void delete(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 id"));
        // 로그인 한 본인 게시글인지 확인하기

        // 활성화된 상태인지 확인
        if (post.getStatus() == Post.PostStatus.DELETED) {
            throw new IllegalArgumentException("이미 삭제된 게시글입니다.");
        }
        post.changeToDelete();
    }

    @Transactional(readOnly = true)
    public Page<PostWithLikeCountResponseDto> findMyPosts(Integer myId, Pageable pageable) {
        return postRepository.findPostsByUserIdWithLikeCount(myId, pageable);
    }

    @Transactional
    public PostResponseDto update(Integer postId, PostCreateRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글"));
        if (post.getStatus() == Post.PostStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 게시글");
        }

        post.update(request.getContent());
        return PostResponseDto.from(post);
    }
}
