package com.geekq.miaosha.common.resultbean;

import com.geekq.miaosha.common.enums.ResultStatus;

import java.io.Serializable;

public class ResultObject<T> extends AbstractResult implements Serializable {
    private static final long serialVersionUID = 867933019328199779L;
    private T data;
    private Integer count;

    protected ResultObject(ResultStatus status, String message) {
        super(status, message);
    }
    protected ResultObject(ResultStatus status) {
        super(status);
    }
    public static <T> ResultObject<T> build() {
        return new ResultObject(ResultStatus.SUCCESS, (String)null);
    }

    public static <T> ResultObject<T> build(String message) {
        return new ResultObject(ResultStatus.SUCCESS, message);
    }

    public static <T> ResultObject<T> error(ResultStatus status) {
        return new ResultObject<T>(status);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void success(T value) {
        this.success();
        this.data = value;
        this.count = 0;
    }

}
