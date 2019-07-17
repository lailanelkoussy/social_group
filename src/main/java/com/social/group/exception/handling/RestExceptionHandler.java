package com.social.group.exception.handling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.io.InvalidClassException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(InvalidClassException.class)
    protected ResponseEntity<Object> handleInvalidClassException(
            InvalidClassException ex) {
        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.NOT_FOUND);
        
    }

    @ExceptionHandler(IllegalAccessException.class)
    protected ResponseEntity<Object> handleIllegalAccessException(IllegalAccessException ex) {
        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.FORBIDDEN);
    }
    


}
