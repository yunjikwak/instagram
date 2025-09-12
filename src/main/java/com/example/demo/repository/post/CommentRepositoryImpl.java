package com.example.demo.repository.post;

import com.example.demo.controller.post.dto.CommentResponseDto;
import com.example.demo.controller.post.dto.CommentSimpleResponseDto;
import com.example.demo.repository.post.entity.Comment;
import com.example.demo.repository.post.entity.QComment;
import com.example.demo.repository.post.entity.QPost;
import com.example.demo.repository.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentSimpleResponseDto> findCommentDtosByPostId(Integer postId, Pageable pageable) {
        QComment qc = QComment.comment;
        QPost qp = QPost.post;
        QUser qu = QUser.user;

        List<CommentSimpleResponseDto> content = queryFactory
                .select(Projections.constructor(
                        CommentSimpleResponseDto.class,
                        qc.id,
                        qc.content,
                        qc.status,
                        qc.createdAt,
                        qu.name
                ))
                .from(qc)
                .leftJoin(qc.user, qu)
                .where(qc.post.id.eq(postId)
                        .and(qc.status.eq(Comment.CommentStatus.ACTIVE)))
                .orderBy(qc.createdAt.desc())
                .groupBy(qc.id)
                .offset(pageable.getOffset())
                .limit((pageable.getPageSize()))
                .fetch();


        Long total = queryFactory
                .select((qc.count()))
                .from(qc)
                .where(qc.post.id.eq(postId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
