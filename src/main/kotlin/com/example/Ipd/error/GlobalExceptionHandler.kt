package com.example.Ipd.error

import com.example.Ipd.error.dto.ErrorResponse
import com.example.Ipd.error.exception.BusinessException

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import jakarta.servlet.http.HttpServletRequest

@RestControllerAdvice
class GlobalExceptionHandler {


    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException::class)
    fun handlerBusinessException(
        ex: BusinessException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val status = ex.httpStatus

        val body = ErrorResponse(
            code = ex.errorCode,
            message = "요청을 처리하지 못했습니다.",
            status = status.value(),
            path = request.requestURI
        )

        return ResponseEntity
            .status(status)
            .body(body)
    }

    /**
     * 그 외 모든 예외 처리 (예상하지 못한 서버 에러)
     */
    @ExceptionHandler(Exception::class)
    fun handlerUnexpectedException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        val status = HttpStatus.INTERNAL_SERVER_ERROR

        val body = ErrorResponse(
            code = "INTERNAL_SERVER_ERROR",
            message = "서버 에러가 발생했습니다. 잠시 후 다시 시도해주세요.",
            status = status.value(),
            path = request.requestURI
        )

        return ResponseEntity
            .status(status)
            .body(body)
    }
}
