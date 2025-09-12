package com.example.demo.common.exception;

import com.example.demo.common.response.BaseResponseStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
// @Setter
    // exception 객체 == 스냅샷, 생성 이후 불변성
// RuntimeException
    // 이 예외를 try-catch로 강제로 잡지 X
public class BaseException extends RuntimeException { // resposneStatus 담기
    private final BaseResponseStatus status;

    public BaseException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
