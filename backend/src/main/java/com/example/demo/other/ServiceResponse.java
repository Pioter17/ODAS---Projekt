package com.example.demo.other;

public class ServiceResponse<T> {
    public T data;
    public Boolean isSuccess;
    public String message;
    public ServiceResponse(T data, Boolean isSuccess, String message) {
        this.data = data;
        this.isSuccess = isSuccess;
        this.message = message;
    }
}