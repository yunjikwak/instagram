package com.example.demo.repository.post;

import com.example.demo.repository.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer>, CommentRepositoryCustom {
}
