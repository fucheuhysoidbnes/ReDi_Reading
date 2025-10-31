package com.example.redi.data;

public interface DataSourceCallback<T> {
    void onSuccess(T data);
    void onError(String error);
}