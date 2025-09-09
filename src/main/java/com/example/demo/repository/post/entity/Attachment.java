package com.example.demo.repository.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment {
    public enum AttachmentStatus {
        ACTIVE,
        DELETED
    }
    public enum AttachmentType {
        IMAGE,
        VIDEO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private AttachmentStatus status;

    @Enumerated(EnumType.STRING)
    private AttachmentType type;

    private String filePath;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // post
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public static Attachment create(AttachmentType type, String filePath, Integer displayOrder, Post post) {
        return new Attachment(
                null,
                AttachmentStatus.ACTIVE,
                type,
                filePath,
                displayOrder,
                LocalDateTime.now(),
                LocalDateTime.now(),
                post
        );
    }

    void setPost(Post post) {
        this.post = post;
    }
}
