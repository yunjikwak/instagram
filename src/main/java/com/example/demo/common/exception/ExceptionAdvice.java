package com.example.demo.common.exception;

import com.example.demo.common.response.BaseResponse;
import com.example.demo.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice { // error catch
    // 1. 직접 정의한 BaseException 처리
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<String>> BaseExceptionHandle(BaseException e) {
        log.warn("BaseException. error message: {}", e.getMessage());
        BaseResponse<String> errorResponse = new BaseResponse<>(e.getStatus());
        return ResponseEntity.status(e.getStatus().getCode()).body(errorResponse);
    }

    // 2. MethodArgumentNotValidException - @RequestBody의 Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<String>> handle(MethodArgumentNotValidException e) {
        log.warn("RequestBody Validation failed. error message: {}", e.getMessage());
        BaseResponse<String> errorResponse = new BaseResponse<>(BaseResponseStatus.INVALID_INPUT_VALUE);
        return ResponseEntity.status(BaseResponseStatus.INVALID_INPUT_VALUE.getCode()).body(errorResponse);
    }

    // 3.  HandlerMethodValidationException - @RequestParam/@PathVariable 등의 Validated
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BaseResponse<String>> handle(HandlerMethodValidationException e) {
        log.warn("Parameter Validation failed. error message: {}", e.getMessage());
        BaseResponse<String> errorResponse = new BaseResponse<>(BaseResponseStatus.INVALID_INPUT_VALUE);
        return ResponseEntity.status(BaseResponseStatus.INVALID_INPUT_VALUE.getCode()).body(errorResponse);
    }

    // 4. 그 외 모든 예외 처리x
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<String>> ExceptionHandle(Exception e) {
        log.error("Exception has occured. ", e);
        BaseResponse<String> errorResponse = new BaseResponse<>(BaseResponseStatus.UNEXPECTED_ERROR);
        return ResponseEntity.status(BaseResponseStatus.UNEXPECTED_ERROR.getCode()).body(errorResponse);
    }
}
