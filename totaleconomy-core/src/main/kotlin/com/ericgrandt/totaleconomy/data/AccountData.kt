package com.ericgrandt.totaleconomy.data

// class AccountData {
//    fun createAccount(
//        playerId: UUID,
//        currencyCode: String,
//        defaultBalance: BigDecimal,
//    ): Result<TEAccount, Throwable> {
//        return runCatching {
//            val insertedRow =
//                AccountTable
//                    .insert {
//                        it[AccountTable.playerId] = playerId.toString()
//                        it[AccountTable.currencyCode] = currencyCode
//                        it[AccountTable.balance] = defaultBalance.toDouble()
//                    }
//
//            AccountTable
//                .selectAll()
//                .where { AccountTable.id eq insertedRow[AccountTable.id] }
//                .single()
//                .toTEAccount()
//        }
//    }
//
//    fun getAccount(
//        playerId: UUID,
//        currencyCode: String,
//    ): Result<TEAccount, Throwable> {
//        return runCatching {
//            AccountTable
//                .selectAll()
//                .where {
//                    (AccountTable.playerId eq playerId.toString()) and (AccountTable.currencyCode eq currencyCode)
//                }.single()
//                .toTEAccount()
//        }
//    }
// }
