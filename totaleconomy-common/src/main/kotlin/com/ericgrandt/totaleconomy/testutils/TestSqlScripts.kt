package com.ericgrandt.totaleconomy.testutils

class TestSqlScripts {
    companion object {
        val createAccountTable = """
            CREATE TABLE IF NOT EXISTS te_account (
                id VARCHAR(36) PRIMARY KEY,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()

        val createBalanceTable = """
            CREATE TABLE IF NOT EXISTS te_balance (
                id VARCHAR(36) DEFAULT random_uuid() PRIMARY KEY,
                account_id VARCHAR(36) NOT NULL,
                balance DECIMAL(38, 2) NOT NULL DEFAULT 0,
                FOREIGN KEY (account_id) REFERENCES te_account(id) ON DELETE CASCADE,
                CONSTRAINT uk_balance UNIQUE(account_id)
            );
        """.trimIndent()

        val initScripts = arrayOf(
            createAccountTable,
            createBalanceTable
        )
    }
}