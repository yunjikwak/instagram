package com.example.demo.repository.post;

import com.example.demo.controller.post.dto.PostWithLikeCountResponseDto;
import com.example.demo.repository.post.entity.Post;
import com.example.demo.repository.post.entity.QLike;
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
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public PostWithLikeCountResponseDto findWithLikeCount(Integer postId) {
        QPost qp = QPost.post;
        QLike ql = QLike.like;
        QUser qu = QUser.user;

        return queryFactory
                .select(Projections.constructor(
                        // Projections
                            // DTO의 생성자 -> 리플렉션(Reflection) 통해 호출하여 DTO 객체를 만듦
                            // 필요한 데이터만 정확히 가져와 성능 좋음
                        PostWithLikeCountResponseDto.class,
                        qp.id,
                        qp.content,
                        qu.name,
                        ql.id.count() // 좋아요 수 카운트

                ))
                .from(qp)
                .leftJoin(qp.user, qu)
                .leftJoin(qp.likes, ql)
                .where(qp.id.eq(postId))
                .groupBy(qp.id, qp.content, qu.name)
                .fetchOne();
    }

    @Override
    public Page<PostWithLikeCountResponseDto> findAllWithLikeCount(Pageable pageable) {
        QPost qp = QPost.post;
        QLike ql = QLike.like;
        QUser qu = QUser.user;

        List<PostWithLikeCountResponseDto> content = queryFactory
                .select(Projections.constructor(
                        PostWithLikeCountResponseDto.class,
                        qp.id,
                        qp.content,
                        qu.name,
                        ql.id.count()
                ))
                .from(qp)
                .leftJoin(qp.user, qu)
                .leftJoin(qp.likes, ql)
                .where(qp.status.eq(Post.PostStatus.ACTIVE)) // visible 한 것들만 가져오기
                .orderBy(qp.createdAt.desc()) // 최신순
                .groupBy(qp.id, qp.content, qu.name)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qp.count())
                .from(qp)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<PostWithLikeCountResponseDto> findPostsByUserIdWithLikeCount(Integer myId, Pageable pageable) {
        QPost qp = QPost.post;
        QLike ql = QLike.like;
        QUser qu = QUser.user;

        List<PostWithLikeCountResponseDto> content = queryFactory
                .select(Projections.constructor(
                        PostWithLikeCountResponseDto.class,
                        qp.id,
                        qp.content,
                        qu.name,
                        ql.id.count()
                ))
                .from(qp)
                .leftJoin(qp.user, qu)
                .leftJoin(qp.likes, ql)
                .where(qu.id.eq(myId)
                        .and(qp.status.eq(Post.PostStatus.ACTIVE))) // visible 만
                .orderBy(qp.createdAt.desc()) // 최신순
                .groupBy(qp.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qp.count())
                .from(qp)
                .where(qp.user.id.eq(myId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
