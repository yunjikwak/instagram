package com.example.demo.service;

import com.example.demo.common.exception.BaseException;
import com.example.demo.common.response.BaseResponseStatus;
import com.example.demo.repository.post.LikeRepository;
import com.example.demo.repository.post.PostRepository;
import com.example.demo.repository.post.entity.Like;
import com.example.demo.repository.post.entity.Post;
import com.example.demo.repository.user.UserRepository;
import com.example.demo.repository.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void like(Integer myId, Integer id) {
        User user = userRepository.findById(myId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_USER));
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_POST));

        // deleted 검토
        if (post.getStatus() == Post.PostStatus.DELETED) {
            throw new BaseException(BaseResponseStatus.POST_CONFLICT);
        }

        // 좋아요 중복 확인
        if (likeRepository.existsByUserIdAndPostId(myId, id)) {
            throw new BaseException(BaseResponseStatus.LIKE_CONFLICT);
        }

        Like like = Like.create(user, post);
        Like created = likeRepository.save(like);
    }

    @Transactional
    public void unlike(Integer myId, Integer id) {
        // 조회 불필요 -> like 조회 하나면 됨
//        User user = userRepository.findById(myId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
//        Post post = postRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글"));

        // 좋아요 존재 확인
        Like like = likeRepository.findByUserIdAndPostId(myId, id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_LIKE));
        likeRepository.delete(like);
    }
}
