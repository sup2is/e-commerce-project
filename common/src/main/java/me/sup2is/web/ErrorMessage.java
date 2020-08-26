package me.sup2is.web;

import lombok.*;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {

    private String name;
    private String message;

    public static ErrorMessage createError(Exception e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.name = e.getClass().getName();
        errorMessage.message = e.getMessage();
        return errorMessage;
    }

    public static List<ErrorMessage> createFieldErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(error -> new ErrorMessage(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
    }


}
