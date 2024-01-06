package com.ericgrandt.totaleconomy.models;

public record TransferResult(ResultType resultType, String message) {
    public enum ResultType {
        SUCCESS,
        FAILURE
    }
}
