package com.example.demo.service;

import com.example.demo.common.exception.BaseException;
import com.example.demo.common.response.BaseResponseStatus;
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

    private static final int MAX_REPORT_COUNT = 5; // 5회 이상 신고 시 숨김 처리

    @Transactional
    public void create(Integer userId, Integer postId, ReportCreateRequestDto request) {
        // 작성자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_USER));
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_POST));

        if (post.getStatus() == Post.PostStatus.DELETED) {
            throw new BaseException(BaseResponseStatus.POST_CONFLICT);
        }
        Report report = Report.create(request.getReason(), user, post);
        reportRepository.save(report);

        // 신고 누적 수 확인
        Integer count = reportRepository.countByPost(post);
        if (count >= MAX_REPORT_COUNT) {
            post.changeToDelete(); // 삭제 == 숨김 처리
            postRepository.save(post);
        }

    }
}
