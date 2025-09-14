package com.ericgrandt.totaleconomy.data

object SqlScripts {
    val createAccountTable = """
        CREATE TABLE IF NOT EXISTS te_account (
            id VARCHAR(36) PRIMARY KEY,
            created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    """.trimIndent()

    val initScripts = arrayOf(
        createAccountTable
    )
}