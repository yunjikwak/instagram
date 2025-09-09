package com.example.demo.repository.post;

import com.example.demo.repository.post.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    boolean existsByUserIdAndPostId(Integer userId, Integer postId);

    Optional<Like> findByUserIdAndPostId(Integer userId, Integer postId);
}
