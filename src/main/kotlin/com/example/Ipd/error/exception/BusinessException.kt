package com.example.Ipd.error.exception

import org.springframework.http.HttpStatus

open class BusinessException(
    val errorCode: String,
    override val message: String,
    val httpStatus: HttpStatus
) : RuntimeException(message)