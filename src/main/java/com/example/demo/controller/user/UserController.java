package com.example.demo.controller.user;

import com.example.demo.controller.post.dto.PostWithLikeCountResponseDto;
import com.example.demo.controller.user.dto.UserLocalCreateRequestDto;
import com.example.demo.controller.user.dto.UserLocalLoginRequestDto;
import com.example.demo.controller.user.dto.UserResponseDto;
import com.example.demo.controller.user.dto.UserSocialCreateRequestDto;
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
    public ResponseEntity<UserResponseDto> user(@PathVariable Integer id) {
        UserResponseDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/local")
    public ResponseEntity<UserResponseDto> localLogin(@RequestBody @Valid UserLocalLoginRequestDto request) {
        UserResponseDto user = userService.localLogin(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/signup/local")
    public ResponseEntity<UserResponseDto> localSignUp(@RequestBody @Valid UserLocalCreateRequestDto request) {
        UserResponseDto user = userService.localSignUp(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/signup/social")
    public ResponseEntity<UserResponseDto> socialSignUp(@RequestBody @Valid UserSocialCreateRequestDto request) {
        UserResponseDto user = userService.socialSignUp(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/terms")
    public ResponseEntity<Void> agreeTerms(@PathVariable Integer id) {
        userService.agreeTerms(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawUser(@PathVariable Integer id) {
        userService.withdrawUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/me/posts")
    public ResponseEntity<Page<PostWithLikeCountResponseDto>> myPosts(
            @AuthenticationPrincipal User loggedInUser,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("잘못된 페이지네이션 값");
        }

        // 사용자 인증 정보 꺼내기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User principal = (User) authentication.getPrincipal();
//        Integer myId = principal.getId();
        Integer myId = loggedInUser.getId();

        Page<PostWithLikeCountResponseDto> posts = postService.findMyPosts(myId, pageable);
        return ResponseEntity.ok(posts);
    }
}
