package com.ericgrandt.totaleconomy.testutils

import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.JdbcTransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.sql.Connection

/**
 * Mocks out the JetBrains Exposed transaction block so you can run unit tests without needing to create an in-mem db
 */
class TestTransactionManager : JdbcTransactionManager {
    override val db: Database = mockk(relaxed = true)
    override var defaultReadOnly: Boolean = false
    override var defaultIsolationLevel: Int = 0
    override var defaultMaxAttempts: Int = 0
    override var defaultMinRetryDelay: Long = 0
    override var defaultMaxRetryDelay: Long = 0

    private val mockDb: Database = mockk(relaxed = true)

    override fun newTransaction(
        isolation: Int,
        readOnly: Boolean,
        outerTransaction: JdbcTransaction?,
    ): JdbcTransaction {
        return transaction()
    }

    fun transaction(): JdbcTransaction {
        return mockk(relaxed = true) {
            every { db } returns mockDb
        }
    }

    fun apply() {
        TransactionManager.registerManager(mockDb, this@TestTransactionManager)
        Database.connect({ mockk<Connection>(relaxed = true) }, null, manager = { this })
    }
}

fun mockTransaction() = TestTransactionManager().apply()