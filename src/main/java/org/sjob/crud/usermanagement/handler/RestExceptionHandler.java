package org.sjob.crud.usermanagement.handler;

import org.sjob.crud.usermanagement.dto.reponse.DtoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeExceptions(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(DtoResponse.builder()
                .message(ex.getMessage())
                .build(), HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(DtoResponse.builder()
                .message(ex.getMessage())
                .build(), HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
