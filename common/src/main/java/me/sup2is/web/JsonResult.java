package me.sup2is.web;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonResult<T> {

    private Result result;
    private List<String> messages;
    private T data;

    public JsonResult(Result result) {
        this(result, null, null);
    }

    public JsonResult(Result result, List<String> messages) {
        this(result, messages, null);
    }

    public JsonResult(T data) {
        this(Result.SUCCESS, null, data);
    }

    public JsonResult(Result result, List<String> messages, T data) {
        this.result = result;
        this.messages = messages;
        this.data = data;
    }

    private enum Result {
        SUCCESS, FAIL
    }

}
