package me.sup2is.exception;

import me.sup2is.web.JsonResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class RestResponseExceptionHandler{

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<JsonResult<?>> badCredentialsExceptionHandle(Exception e, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(new JsonResult<>(e), HttpStatus.UNAUTHORIZED);
    }
}
