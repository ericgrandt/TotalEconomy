package com.ericgrandt.totaleconomy.common.econ;

public record TransactionResult(ResultType resultType, String message) {
    public enum ResultType {
        SUCCESS,
        FAILURE
    }
}
