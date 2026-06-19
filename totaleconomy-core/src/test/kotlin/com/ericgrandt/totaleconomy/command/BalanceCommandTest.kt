package com.ericgrandt.totaleconomy.command

// class BalanceCommandTest {
//    @MockK
//    lateinit var econMock: EconomyProvider
//
//    @MockK
//    lateinit var playerMock: Player
//
//    @BeforeTest
//    fun setup() {
//        MockKAnnotations.init(this, relaxUnitFun = true)
//    }
//
//    @Test
//    @Tag("Unit")
//    fun `execute with success and currencyCode parameter should send the correct message to the player`() {
//        runTest {
//            // Arrange
//            val playerId = UUID.randomUUID()
//            val account = TEAccount(playerId, "USD", BigDecimal.valueOf(1.236))
//            val currency = TECurrency("USD", "Dollar", "Dollars", "$", 2, true)
//            every { econMock.getAccount(any(), any()) } returns account
//            every { econMock.getCurrency("USD") } returns currency
//            every { playerMock.uniqueId } returns UUID.randomUUID()
//
//            val sut = BalanceCommand(econMock)
//
//            // Act
//            val actual = sut.execute(playerMock, mutableMapOf("currencyCode" to CommandArgument.StringParam("USD")))
//            val expected = CommandResult.SUCCESS
//
//            // Assert
//            assertEquals(expected, actual)
//            verify(exactly = 1) {
//                playerMock.sendMessage(
//                    Component.text("Balance: ").append(Component.text("$1.23")),
//                )
//            }
//        }
//    }
//
//    @Test
//    @Tag("Unit")
//    fun `execute with success and no currencyCode parameter should use default currency and send the correct message to the player`() {
//        runTest {
//            // Arrange
//            val playerId = UUID.randomUUID()
//            val account = TEAccount(playerId, "USD", BigDecimal.valueOf(1.236))
//            val currency = TECurrency("USD", "Dollar", "Dollars", "$", 2, true)
//            every { econMock.getAccount(any(), any()) } returns account
//            every { econMock.getDefaultCurrency() } returns currency
//            every { playerMock.uniqueId } returns UUID.randomUUID()
//
//            val sut = BalanceCommand(econMock)
//
//            // Act
//            val actual = sut.execute(playerMock, mutableMapOf())
//            val expected = CommandResult.SUCCESS
//
//            // Assert
//            assertEquals(expected, actual)
//            verify(exactly = 1) {
//                playerMock.sendMessage(
//                    Component.text("Balance: ").append(Component.text("$1.23")),
//                )
//            }
//        }
//    }
//
//    @Test
//    @Tag("Unit")
//    fun `execute with error should send the correct message to the player`() {
//        runTest {
//            // Arrange
//            val currency = TECurrency("USD", "Dollar", "Dollars", "$", 2, true)
//            every { econMock.getDefaultCurrency() } returns currency
//            every { econMock.getAccount(any(), any()) } throws AccountNotFoundException()
//            every { playerMock.uniqueId } returns UUID.randomUUID()
//
//            val sut = BalanceCommand(econMock)
//
//            // Act
//            val actual = sut.execute(playerMock, mutableMapOf())
//            val expected = CommandResult.FAILURE
//
//            // Assert
//            assertEquals(expected, actual)
//            verify(exactly = 1) {
//                playerMock.sendMessage(
//                    Component.text("You don't have an account for this currency.").color(NamedTextColor.YELLOW),
//                )
//            }
//        }
//    }
// }
