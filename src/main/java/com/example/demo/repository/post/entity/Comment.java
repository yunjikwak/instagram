package com.example.demo.repository.post.entity;

import com.example.demo.repository.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    public enum CommentStatus {
        ACTIVE,
        DELETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String content; // ~  200자

    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 부모 댓글
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 대댓글(자식)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children;

    public static Comment create(String content, Post post, User user) {
        return new Comment(
                null,
                content,
                CommentStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                post,
                user,
                null,
                new ArrayList<>()
        );
    }

    public static Comment createChild(String content, Post post, User user, Comment parent) {
        return new Comment(
                null,
                content,
                CommentStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                post,
                user,
                parent,
                new ArrayList<>()
        );
    }

    public void addChildComment(Comment child) {
        this.children.add(child);
        child.addParent(this);
    }

    public void addParent(Comment parent) {
        this.parent = parent;
    }

    public void update(
            @NotNull
            @Size(min = 1, max = 200)
            String content
    ) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeToDelete() {
        this.status = CommentStatus.DELETED;
    }
}
