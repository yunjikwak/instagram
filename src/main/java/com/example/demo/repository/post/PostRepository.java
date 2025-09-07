package com.example.demo.repository.post;

import com.example.demo.repository.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer>, PostRepositoryCustom {
}
