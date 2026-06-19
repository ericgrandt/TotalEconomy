package com.ericgrandt.totaleconomy.data

// class AccountDataTest {
//    @Test
//    @Tag("Integration")
//    fun `createAccount with success should return account`() {
//        // Arrange
//        TestUtils.connectToTestDb()
//        val currency = TestUtils.seedDefaultCurrency()
//        val playerId = UUID.randomUUID()
//
//        val sut = AccountData()
//
//        // Act/Assert
//        transaction {
//            val actual = sut.createAccount(playerId, currency.code, BigDecimal.TEN)
//            val expected = Ok(TEAccount(playerId, currency.code, BigDecimal.valueOf(10.0)))
//
//            assertEquals(expected, actual)
//        }
//    }
//
//    @Test
//    @Tag("Integration")
//    fun `createAccount with error should return error result`() {
//        // Arrange
//        TestUtils.connectToTestDb(false)
//
//        val sut = AccountData()
//
//        // Act/Assert
//        transaction {
//            val actual = sut.createAccount(UUID.randomUUID(), "", BigDecimal.TEN)
//
//            assertThat(actual.getError())
//                .isInstanceOf(SQLException::class.java)
//        }
//    }
//
//    @Test
//    @Tag("Integration")
//    fun `getAccount with success should return account`() {
//        // Arrange
//        TestUtils.connectToTestDb()
//        val currency = TestUtils.seedDefaultCurrency()
//        val account = TestUtils.seedAccount(currency.code)
//
//        val sut = AccountData()
//
//        // Act/Assert
//        transaction {
//            val actual = sut.getAccount(account.playerId, currency.code)
//            val expected = Ok(TEAccount(account.playerId, currency.code, BigDecimal.valueOf(10.0)))
//
//            assertEquals(expected, actual)
//        }
//    }
//
//    @Test
//    @Tag("Integration")
//    fun `getAccount with error should return error result`() {
//        // Arrange
//        TestUtils.connectToTestDb(false)
//
//        val sut = AccountData()
//
//        // Act/Assert
//        transaction {
//            val actual = sut.getAccount(UUID.randomUUID(), "")
//
//            assertThat(actual.getError())
//                .isInstanceOf(SQLException::class.java)
//        }
//    }
// }
