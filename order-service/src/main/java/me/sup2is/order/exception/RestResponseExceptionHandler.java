package me.sup2is.order.exception;

import me.sup2is.web.JsonResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OutOfStockException.class)
    protected ResponseEntity<JsonResult<?>> outOfStockExceptionHandle(Exception e, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(new JsonResult<>(e), HttpStatus.BAD_REQUEST);
    }
}
