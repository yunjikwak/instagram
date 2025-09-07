package com.example.demo.repository.post;

import com.example.demo.repository.post.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Integer> {
}
