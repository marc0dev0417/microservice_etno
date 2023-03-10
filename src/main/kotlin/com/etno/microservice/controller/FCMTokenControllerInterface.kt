package com.etno.microservice.controller

import com.etno.microservice.exception.HandleResponse
import com.etno.microservice.model.dto.FCMTokenDTO
import com.etno.microservice.model.dto.SectionDTO
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Controller
interface FCMTokenControllerInterface {
    @ApiOperation(
        value = "get all FCM Tokens registered",
        nickname = "getFCMTokens",
        notes = "Show all FCM Tokens",
        tags = ["FCMToken"],
        response = FCMTokenDTO::class
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "FCMToken", response = FCMTokenDTO::class),
            ApiResponse(code = 400, message = "Bad Request", response = HandleResponse::class),
            ApiResponse(code = 401, message = "Unauthorized", response = HandleResponse::class),
            ApiResponse(code = 403, message = "Forbidden", response = HandleResponse::class),
            ApiResponse(code = 500, message = "Server Error", response = HandleResponse::class)
        ]
    )
    @RequestMapping(
        value = ["/FCMTokens"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    fun getFCMToken(): ResponseEntity<List<FCMTokenDTO>>?

    @ApiOperation(
        value = "save a FCM Token",
        nickname = "saveFCMToken",
        notes = "Save a FCM Token",
        tags = ["FCMToken"],
        response = FCMTokenDTO::class
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "FCMToken", response = FCMTokenDTO::class),
            ApiResponse(code = 400, message = "Bad Request", response = HandleResponse::class),
            ApiResponse(code = 401, message = "Unauthorized", response = HandleResponse::class),
            ApiResponse(code = 403, message = "Forbidden", response = HandleResponse::class),
            ApiResponse(code = 500, message = "Server Error", response = HandleResponse::class)
        ]
    )
    @RequestMapping(
        value = ["/FCMTokens"],
        produces = ["application/json"],
        consumes = ["application/json"],
        method = [RequestMethod.POST]
    )
    fun saveFCMToken(@RequestBody fcmTokenDTO: FCMTokenDTO): ResponseEntity<FCMTokenDTO>?
}