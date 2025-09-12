package com.example.demo.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseStatus { // ExceptionType 정의
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /**
     * 400 : Request, Response 오류
     */
    // 공통
    INVALID_INPUT_VALUE(false, 400, "입력값이 올바르지 않습니다."), // valid error

    // User 관련 에러
    DUPLICATE_USERNAME(false, 400, "중복된 아이디입니다."),
    DUPLICATE_SOCIAL_ID(false, 400, "중복된 소셜 아이디입니다."),
    NOT_FIND_USER(false,HttpStatus.NOT_FOUND.value(),"일치하는 유저가 없습니다."),
    FAILED_TO_LOGIN(false,HttpStatus.NOT_FOUND.value(),"없는 아이디거나 비밀번호가 틀렸습니다."),
    INACTIVE_USER(false, 403, "로그인할 수 없는 계정입니다."), // 차단만? -> 탈퇴 + 차단
    TERMS_NOT_AGREED(false, 403, "약관 동의가 필요합니다."),
    USER_CONFLICT(false, 409, "이미 탈퇴 처리된 계정입니다."),
    INVALID_OAUTH_TYPE(false, HttpStatus.BAD_REQUEST.value(), "알 수 없는 소셜 로그인 형식입니다."),

    // Post 관련 에러
    NOT_FIND_POST(false,HttpStatus.NOT_FOUND.value(),"게시물을 찾을 수 없습니다."),
    FILE_COUNT_EXCEEDED(false, 400, "파일은 최대 10개까지 첨부할 수 있습니다."),
    POST_CONFLICT(false, 409, "이미 삭제된 게시물입니다."),
    LIKE_CONFLICT(false, 409, "이미 '좋아요'를 누른 게시물입니다."),
    NOT_FIND_LIKE(false, 404, "좋아요 기록이 존재하지 않습니다."),

    // Comment 관련 에러
    NOT_FOUND_COMMENT(false, 404, "댓글을 찾을 수 없습니다."),
    COMMENT_CONFLICT(false, 409, "이미 삭제된 댓글입니다."),

    // 권한
    EMPTY_JWT(false, HttpStatus.UNAUTHORIZED.value(), "JWT를 입력해주세요."),
    INVALID_JWT(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 JWT입니다."),
    UNAUTHORIZED_USER(false, 401, "로그인이 필요합니다."),
    FORBIDDEN_ACCESS(false, 403, "접근 권한이 없습니다."),

    /**
     * 500 :  Database, Server 오류
     */
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버와의 연결에 실패하였습니다."),
    PASSWORD_ENCRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 암호화에 실패하였습니다."),

    UNEXPECTED_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상치 못한 에러가 발생했습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}