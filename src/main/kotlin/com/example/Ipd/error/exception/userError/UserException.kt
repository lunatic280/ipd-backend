package com.example.Ipd.error.exception.userError

import com.example.Ipd.error.exception.BusinessException
import org.springframework.http.HttpStatus

class UserNotEqualsException(): BusinessException(
    errorCode = "USER_NOT_EQUALS",
    message = "작성자와 유저가 다릅니다.",
    httpStatus = HttpStatus.FORBIDDEN
)