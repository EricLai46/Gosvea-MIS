package org.gosvea.pojo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;
@NoArgsConstructor

public class Result<T> {
    private Integer code;
    private String message;
    private T data;


    private Map<Integer,String> warnings;




    public Result(Integer code, String message, T data, Map<Integer,String> warnings) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.warnings = warnings;

    }

    public static Result error(String errormessage) {
        return new Result<>(1, errormessage, null, null);
    }

    public static <E> Result<E> error(String errormessage, Map<Integer,String> warnings, String warning) {
        return new Result<>(1, errormessage, null, warnings);
    }

    public static <E> Result<E> success(E data) {
        return new Result<>(0, "success", data, null);
    }

    public static Result success() {
        return new Result<>(0, "success", null, null);
    }

    public static <E> Result<E> success(E data, Map<Integer,String> warnings) {
        return new Result<>(0, "success", data, warnings);
    }

    // Getters and setters
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<Integer,String> getWarnings() {
        return warnings;
    }

    public void setWarnings(Map<Integer,String> warnings) {
        this.warnings = warnings;
    }


}
