package com.etno.microservice.exception

import com.etno.microservice.exception.handler.BadRequestException
import com.etno.microservice.exception.handler.ListEmptyException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class CustomExceptionHandler {
    //Exception to code error 402 ->
    @ExceptionHandler(IOException::class)
    fun handleAccessDeniedException(res: HttpServletResponse) {
        res.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied")
    }

    //Exception to code error 500 ->


    //When he finds nothing ->
    @ExceptionHandler(ListEmptyException::class)
    fun handleNotFoundException(ex: Exception, request: WebRequest): ResponseEntity<Any>{
        val handleMessage = HandleResponse(Constants.LIST_EMPTY.code, Constants.LIST_EMPTY.message)
        return ResponseEntity(handleMessage, HttpHeaders(), HttpStatus.OK)
    }

    //When there is malformation in the request ->
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(): ResponseEntity<Any>{
        val handleMessage = HandleResponse(Constants.BAD_REQUEST.code, Constants.BAD_REQUEST.message)
        return ResponseEntity(handleMessage, HttpHeaders(), HttpStatus.BAD_REQUEST)
    }
}