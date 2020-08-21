package me.sup2is.web;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {

    private String name;
    private String message;

    public static ErrorMessage createError(Exception e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.name = e.getClass().getName();
        errorMessage.message = e.getMessage();
        return errorMessage;
    }
}
