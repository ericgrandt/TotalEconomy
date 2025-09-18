package com.ericgrandt.totaleconomy.data

class TestSqlScripts {
    companion object {
        val createAccountTable = """
            CREATE TABLE IF NOT EXISTS te_account (
                id VARCHAR(36) PRIMARY KEY,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()

        val initScripts = arrayOf(
            createAccountTable
        )
    }
}