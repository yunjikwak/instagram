package com.example.demo.repository;

import com.example.demo.controller.post.dto.PostWithLikeCountResponseDto;
import com.example.demo.repository.post.entity.Attachment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachmentResponseDto {
    private Integer id;
    private Attachment.AttachmentType type;
    private String filePath;
    private Integer displayOrder;

    public static AttachmentResponseDto from(Attachment entity) {
        return new AttachmentResponseDto(
                entity.getId(),
                entity.getType(),
                entity.getFilePath(),
                entity.getDisplayOrder()
        );
    }
}
