package com.badeling.msbot.domain;

public class Result<T> {

    /**
     * 数据
     */
    private T data;

    /**
     * 响应码
     */
    private int retCode;

    /**
     * 状态
     */
    private String status;

    public Result() {

    }

    public Result(T data, int retCode, String status) {
        this.data = data;
        this.retCode = retCode;
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Result{" +
                "data=" + data +
                ", retCode=" + retCode +
                ", status='" + status + '\'' +
                '}';
    }
}