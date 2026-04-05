package com.michelfilho.cnabprocessorapi.controller;

import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(JobInstanceAlreadyCompleteException.class)
    public ResponseEntity handleFileAlreadyImported(JobInstanceAlreadyCompleteException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("This archive was already imported.");
    }

}
