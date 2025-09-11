package com.example.demo.service;

import com.example.demo.controller.post.dto.ReportCreateRequestDto;
import com.example.demo.repository.post.PostRepository;
import com.example.demo.repository.post.ReportRepository;
import com.example.demo.repository.post.entity.Post;
import com.example.demo.repository.post.entity.Report;
import com.example.demo.repository.user.UserRepository;
import com.example.demo.repository.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void create(Integer userId, Integer postId, ReportCreateRequestDto request) {
        // 작성자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글"));

        if (post.getStatus() == Post.PostStatus.DELETED) {
            throw new IllegalArgumentException("삭제된 게시글");
        }
        Report report = Report.create(request.getReason(), user, post);
        reportRepository.save(report);
    }
}
