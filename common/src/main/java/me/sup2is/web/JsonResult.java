package me.sup2is.web;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.validation.FieldError;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
//todo 추후에 builder 패턴 고려할것
public class JsonResult<T> {

    private Result result;
    private List<String> messages;
    private ErrorMessage error;
    private List<ErrorMessage> fieldErrors;
    private T data;

    public JsonResult(Result result) {
        this(result, null, null, null, null);
    }

    public JsonResult(Exception e) {
        this(Result.FAIL
            , null
            , ErrorMessage.createError(e)
            , null
            , null);
    }

    public JsonResult(List<FieldError> fieldErrors) {
        this(Result.FAIL, null, null, ErrorMessage.createFieldErrors(fieldErrors), null);
    }

    public JsonResult(T data) {
        this(Result.SUCCESS, null, null, null, data);
    }

    public JsonResult(Result result, List<String> messages, ErrorMessage error, List<ErrorMessage> fieldErrors, T data) {
        this.result = result;
        this.messages = messages;
        this.error = error;
        this.fieldErrors = fieldErrors;
        this.data = data;
    }

    public enum Result {
        SUCCESS, FAIL
    }

}
