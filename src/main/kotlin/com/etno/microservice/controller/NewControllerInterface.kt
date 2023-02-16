package com.etno.microservice.controller

import com.etno.microservice.exception.HandleResponse
import com.etno.microservice.model.dto.NewDTO
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Controller
interface NewControllerInterface {
    @ApiOperation(
        value = "Get all news",
        nickname = "getNews",
        notes = "Will show all news",
        tags = ["New"],
        response = NewDTO::class
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "New", response = NewDTO::class),
            ApiResponse(code = 400, message = "Bad Request", response = HandleResponse::class),
            ApiResponse(code = 401, message = "Unauthorized", response = HandleResponse::class),
            ApiResponse(code = 403, message = "Forbidden", response = HandleResponse::class),
            ApiResponse(code = 500, message = "Server error", response = HandleResponse::class)
        ]
    )
    @RequestMapping(
        value = ["/news"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    fun getNews(): ResponseEntity<List<NewDTO>>

    @ApiOperation(
        value = "Get all news by username",
        nickname = "getNews",
        notes = "Will find news by username",
        tags = ["New"],
        response = NewDTO::class
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 201, message = "New", response = NewDTO::class),
            ApiResponse(code = 400, message = "Bad Request", response = HandleResponse::class),
            ApiResponse(code = 401, message = "Unauthorized", response = HandleResponse::class),
            ApiResponse(code = 403, message = "Forbidden", response = HandleResponse::class),
            ApiResponse(code = 500, message = "Server error", response = HandleResponse::class)
        ]
    )
    @RequestMapping(
        value = ["/news"],
        produces = ["application/json"],
        params = ["username"],
        method = [RequestMethod.GET]
    )
    fun getNewsByUsername(
        @RequestParam(name = "username", required = true) username: String
    ): ResponseEntity<List<NewDTO>>

    @ApiOperation(
            value = "Get all news by username and category",
            nickname = "getNewsByUsernameAndCategory",
            notes = "You gonna see news by username and category",
            tags = ["New"],
            response = NewDTO::class
    )
    @ApiResponses(
            value = [
                ApiResponse(code = 201, message = "New", response = NewDTO::class),
                ApiResponse(code = 400, message = "Bad Request", response = HandleResponse::class),
                ApiResponse(code = 401, message = "Unauthorized", response = HandleResponse::class),
                ApiResponse(code = 403, message = "Forbidden", response = HandleResponse::class),
                ApiResponse(code = 500, message = "Server error", response = HandleResponse::class)
            ]
    )
    @RequestMapping(
            value = ["/news"],
            produces = ["application/json"],
            params = ["username", "category"],
            method = [RequestMethod.GET]
    )
    fun findNewsByUsernameAndCategory(
            @RequestParam(name = "username", required = true) username: String,
            @RequestParam(name = "category", required = true) category: String
    ): ResponseEntity<List<NewDTO>>
}