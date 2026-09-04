package com.example.Ipd.error.exception.placeError

import com.example.Ipd.error.exception.BusinessException
import org.springframework.http.HttpStatus

class CommentNotFoundException(commentId: Long) : BusinessException(
    errorCode = "COMMENT_NOT_FOUND",
    message = "해당 댓글을 찾을 수 없습니다: id=$commentId",
    httpStatus = HttpStatus.NOT_FOUND
)

class CommentAccessDeniedException : BusinessException(
    errorCode = "COMMENT_ACCESS_DENIED",
    message = "해당 댓글의 작성자만 관리할 수 있습니다.",
    httpStatus = HttpStatus.FORBIDDEN
)