package com.example.rxjava;

public interface ObservableOnSubscribe<T> {
    void subscribe(Observer<? super T> observer);
}
