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
public class Post {
    public enum PostStatus {
        ACTIVE,
        DELETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 작성자
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 좋아요 -> 양방향? X, DTO + querydsl -> 좋아요 수만 담은 결과 받아오기
        // 개별저장소 -> redis? or cache or ele
    // 유저 수 적으면 카운팅 / @Cacheable
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    // attachment
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;

    public static Post create(String content, User user) {
        return new Post(
                null,
                content,
                PostStatus.ACTIVE,
                LocalDateTime.now(), // 업데이트 시각도 초기 시각으로 시작
                LocalDateTime.now(),
                user,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public void changeToDelete() {
        this.status = PostStatus.DELETED;
    }

    public void update(
            @NotNull
            @Size(min = 1, max = 200)
            String content
    ) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setPost(this);
    }
}
