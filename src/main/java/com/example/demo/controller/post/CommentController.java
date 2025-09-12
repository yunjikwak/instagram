package com.example.demo.controller.post;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.controller.post.dto.CommentCreateRequestDto;
import com.example.demo.controller.post.dto.CommentResponseDto;
import com.example.demo.repository.user.entity.User;
import com.example.demo.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CommentResponseDto>> comment(@PathVariable Integer id) {
        CommentResponseDto comment = commentService.findById(id);
        return ResponseEntity.ok(new BaseResponse<>(comment));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<CommentResponseDto>> update(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable Integer id,
            @RequestBody @Valid CommentCreateRequestDto request
    )  {
        // 사용자 인증 정보 꺼내기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User principal = (User) authentication.getPrincipal();
//        Integer myId = principal.getId();

        Integer myId = loggedInUser.getId();

        CommentResponseDto updatedComment = commentService.update(myId, id, request);
        return ResponseEntity.ok(new BaseResponse<>(updatedComment));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<BaseResponse<CommentResponseDto>> createReply(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable Integer id,
            @RequestBody @Valid CommentCreateRequestDto request
    ) {
        Integer myId = loggedInUser.getId();
        CommentResponseDto comment = commentService.createChild(myId, id, request);
        return ResponseEntity.ok(new BaseResponse<>(comment));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<Void>> delete(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable Integer id
    ) {
        Integer myId = loggedInUser.getId();
        commentService.delete(myId, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
