package com.ericgrandt.impl;

import org.spongepowered.api.service.economy.account.AccountDeletionResultType;

public class AccountDeletionResultTypeImpl implements AccountDeletionResultType {
    private final boolean isSuccess;

    public AccountDeletionResultTypeImpl(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AccountDeletionResultTypeImpl that = (AccountDeletionResultTypeImpl) o;

        return isSuccess == that.isSuccess;
    }

    @Override
    public int hashCode() {
        return (isSuccess ? 1 : 0);
    }
}
