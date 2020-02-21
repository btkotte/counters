package com.example.counters.exception;

public class CounterLimitReachedException extends RuntimeException {

    public CounterLimitReachedException(String message) {
        super(message);
    }
}