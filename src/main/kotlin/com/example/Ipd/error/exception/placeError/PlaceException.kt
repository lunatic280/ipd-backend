package com.example.Ipd.error.exception.placeError

import com.example.Ipd.error.exception.BusinessException
import org.springframework.http.HttpStatus

class PlaceNotFoundException(placeId: Long) : BusinessException(
    errorCode = "PLACE_NOT_FOUND",
    message = "해당 장소를 찾을 수 없습니다: id=$placeId",
    httpStatus = HttpStatus.NOT_FOUND
)

class PlaceAccessDeniedException : BusinessException(
    errorCode = "PLACE_ACCESS_DENIED",
    message = "본인이 작성한 장소만 관리할 수 있습니다",
    httpStatus = HttpStatus.FORBIDDEN
)
