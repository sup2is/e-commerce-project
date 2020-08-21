package me.sup2is.web;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonResult<T> {

    private Result result;
    private List<String> messages;
    private ErrorMessage errors;
    private T data;

    public JsonResult(Result result) {
        this(result, null, null, null);
    }

    public JsonResult(Exception e) {
        this(Result.FAIL
            , null
            , ErrorMessage.createError(e)
            , null);

    }

    public JsonResult(T data) {
        this(Result.SUCCESS, null, null, data);
    }

    public JsonResult(Result result, List<String> messages, ErrorMessage error, T data) {
        this.result = result;
        this.messages = messages;
        this.errors = error;
        this.data = data;
    }

    private enum Result {
        SUCCESS, FAIL
    }

}
