package com.example.demo.controller.user;

import com.example.demo.common.exception.BaseException;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.common.response.BaseResponseStatus;
import com.example.demo.controller.post.dto.PostWithLikeCountResponseDto;
import com.example.demo.controller.user.dto.*;
import com.example.demo.repository.user.entity.User;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponseDto>> user(@PathVariable Integer id) {
        UserResponseDto user = userService.findById(id);
        return ResponseEntity.ok(new BaseResponse<>(user));
    }

    @PostMapping("/local")
    public ResponseEntity<BaseResponse<UserLoginResponseDto>> localLogin(@RequestBody @Valid UserLocalLoginRequestDto request) {
        UserLoginResponseDto user = userService.localLogin(request);
        return ResponseEntity.ok(new BaseResponse<>(user));
    }

    @PostMapping("/signup/local")
    public ResponseEntity<BaseResponse<UserResponseDto>> localSignUp(@RequestBody @Valid UserLocalCreateRequestDto request) {
        UserResponseDto user = userService.localSignUp(request);
        return ResponseEntity.ok(new BaseResponse<>(user));
    }

    @PostMapping("/signup/social")
    public ResponseEntity<BaseResponse<UserResponseDto>> socialSignUp(@RequestBody @Valid UserSocialCreateRequestDto request) {
        UserResponseDto user = userService.socialSignUp(request);
        return ResponseEntity.ok(new BaseResponse<>(user));
    }

    @PostMapping("/me/terms")
    public ResponseEntity<BaseResponse<Void>> agreeTerms(
            @AuthenticationPrincipal User loggedInUser
    ) {
        Integer myId = loggedInUser.getId();
        userService.agreeTerms(myId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me")
    public ResponseEntity<BaseResponse<UserResponseDto>> update(
            @AuthenticationPrincipal User loggedInUser,
            @RequestBody @Valid UserUpdateRequestDto request
    ) {
        Integer myId = loggedInUser.getId();
        UserResponseDto user = userService.update(myId, request);
        return ResponseEntity.ok(new BaseResponse<>(user));
    }

    @PatchMapping("/me/status")
    public ResponseEntity<BaseResponse<Void>> withdrawUser(
            @AuthenticationPrincipal User loggedInUser
    ) {
        Integer myId = loggedInUser.getId();
        userService.withdrawUser(myId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/me/posts")
    public ResponseEntity<BaseResponse<Page<PostWithLikeCountResponseDto>>> myPosts(
            @AuthenticationPrincipal User loggedInUser,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new BaseException(BaseResponseStatus.INVALID_INPUT_VALUE);
        }

        // 사용자 인증 정보 꺼내기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User principal = (User) authentication.getPrincipal();
//        Integer myId = principal.getId();
        Integer myId = loggedInUser.getId();

        Page<PostWithLikeCountResponseDto> posts = postService.findMyPosts(myId, pageable);
        return ResponseEntity.ok(new BaseResponse<>(posts));
    }
}
