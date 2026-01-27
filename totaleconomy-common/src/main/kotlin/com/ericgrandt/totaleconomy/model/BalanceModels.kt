package com.ericgrandt.totaleconomy.model

import java.util.UUID

data class SetBalance(val accountId: UUID, val balance: Double)

data class WithdrawFromBalance(val accountId: UUID, val amount: Double)

data class DepositIntoBalance(val accountId: UUID, val amount: Double)

data class TransferBalance(val fromAccountId: UUID, val toAccountId: UUID, val amount: Double)