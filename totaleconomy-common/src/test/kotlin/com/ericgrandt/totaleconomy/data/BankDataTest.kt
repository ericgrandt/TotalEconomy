package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.testutils.TestUtils
import com.github.michaelbull.result.Ok
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Tag
import kotlin.test.Test
import kotlin.test.assertEquals

class BankDataTest {
    @Test
    @Tag("Integration")
    fun createBank_WithSuccess_ShouldReturnCreatedRowCount() {
        // Arrange
        TestUtils.connectToTestDb()
        val account = TestUtils.seedAccount()

        val sut = BankData()

        // Act/Assert
        transaction {
            val actual = sut.createBank(account.id)
            val expected = Ok(1)

            assertEquals(expected, actual)
        }
    }
}
