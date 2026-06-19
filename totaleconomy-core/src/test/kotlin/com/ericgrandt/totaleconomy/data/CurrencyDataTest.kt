package com.ericgrandt.totaleconomy.data

// class CurrencyDataTest {
//    @Test
//    @Tag("Integration")
//    fun `getDefaultCurrency with success should return default currency`() {
//        // Arrange
//        TestUtils.connectToTestDb()
//        TestUtils.seedDefaultCurrency()
//
//        val sut = CurrencyData()
//
//        // Act/Assert
//        transaction {
//            val actual = sut.getDefaultCurrency()
//            val expectedTECurrency =
//                TECurrency(
//                    "USD",
//                    "Dollar",
//                    "Dollars",
//                    "$",
//                    2,
//                    true,
//                )
//            val expected = Ok(expectedTECurrency)
//
//            assertEquals(expected, actual)
//        }
//    }
//
//    @Test
//    @Tag("Integration")
//    fun `getDefaultCurrency with error should return error result`() {
//        // Arrange
//        TestUtils.connectToTestDb(false)
//
//        val sut = CurrencyData()
//
//        // Act/Assert
//        transaction {
//            val actual = sut.getDefaultCurrency()
//
//            assertThat(actual.getError())
//                .isInstanceOf(SQLException::class.java)
//        }
//    }
//
//    @Test
//    @Tag("Integration")
//    fun `getCurrency with success should return currency`() {
//        // Arrange
//        TestUtils.connectToTestDb()
//        TestUtils.seedDefaultCurrency()
//
//        val sut = CurrencyData()
//
//        // Act/Assert
//        transaction {
//            val actual = sut.getCurrency("USD")
//            val expectedTECurrency =
//                TECurrency(
//                    "USD",
//                    "Dollar",
//                    "Dollars",
//                    "$",
//                    2,
//                    true,
//                )
//            val expected = Ok(expectedTECurrency)
//
//            assertEquals(expected, actual)
//        }
//    }
//
//    @Test
//    @Tag("Integration")
//    fun `getCurrency with error should return error result`() {
//        // Arrange
//        TestUtils.connectToTestDb(false)
//
//        val sut = CurrencyData()
//
//        // Act/Assert
//        transaction {
//            val actual = sut.getCurrency("")
//
//            assertThat(actual.getError())
//                .isInstanceOf(SQLException::class.java)
//        }
//    }
//
//    @Test
//    @Tag("Integration")
//    fun `getCurrencyList with success should return currency list`() {
//        // Arrange
//        TestUtils.connectToTestDb()
//        TestUtils.seedDefaultCurrency()
//
//        val sut = CurrencyData()
//
//        // Act/Assert
//        transaction {
//            val actual = sut.getCurrencyList()
//            val expectedTECurrency =
//                TECurrency(
//                    "USD",
//                    "Dollar",
//                    "Dollars",
//                    "$",
//                    2,
//                    true,
//                )
//            val expected = Ok(listOf(expectedTECurrency))
//
//            assertEquals(expected, actual)
//        }
//    }
//
//    @Test
//    @Tag("Integration")
//    fun `getCurrencyList with error should return error result`() {
//        // Arrange
//        TestUtils.connectToTestDb(false)
//
//        val sut = CurrencyData()
//
//        // Act/Assert
//        transaction {
//            val actual = sut.getCurrencyList()
//
//            assertThat(actual.getError())
//                .isInstanceOf(SQLException::class.java)
//        }
//    }
// }
