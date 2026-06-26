package com.ericgrandt.totaleconomy.command

// class BalanceCommandExecutorTest {
//    @MockK
//    lateinit var loggerMock: Logger
//
//    @MockK
//    lateinit var playerMock: PlayerEntity
//
//    @MockK
//    lateinit var commandMock: Command
//
//    @BeforeTest
//    fun setUp() {
//        MockKAnnotations.init(this, relaxUnitFun = true)
//    }
//
//    @Test
//    @Tag("Integration")
//    fun `onCommand with no currency code argument should send default balance to player`() {
//        runTest {
//            // Arrange
//            TestUtils.startTestDb()
//            val currency = TestUtils.seedDefaultCurrency()
//            val account = TestUtils.seedAccount(currency.code)
//
//            every { playerMock.uniqueId } returns account.playerId
//            every { playerMock.name } returns "TestName"
//
//            val economy = EconomyProvider(loggerMock, AccountData(), CurrencyData())
//            val balanceCommand = BalanceCommand(economy, StandardTestDispatcher(testScheduler))
//
//            val sut = BalanceCommandExecutor(this, balanceCommand)
//
//            // Act
//            sut.onCommand(playerMock, commandMock, "", emptyArray())
//
//            // Assert
//            testScheduler.advanceUntilIdle()
//            verify(exactly = 1) {
//                playerMock.sendMessage(
//                    Component.text("Balance: ").append(Component.text("$10.00")),
//                )
//            }
//        }
//    }
//
//    @Test
//    @Tag("Integration")
//    fun `onCommand with currency code argument should send currency balance to player`() {
//        runTest {
//            // Arrange
//            TestUtils.startTestDb()
//            val defaultCurrency = TestUtils.seedDefaultCurrency()
//            val currency = TestUtils.seedNonDefaultCurrency()
//            val defaultAccount = TestUtils.seedAccount(defaultCurrency.code)
//            TestUtils.seedAccount(currency.code)
//
//            every { playerMock.uniqueId } returns defaultAccount.playerId
//            every { playerMock.name } returns "TestName"
//
//            val economy = EconomyProvider(loggerMock, AccountData(), CurrencyData())
//            val balanceCommand = BalanceCommand(economy, StandardTestDispatcher(testScheduler))
//
//            val sut = BalanceCommandExecutor(this, balanceCommand)
//
//            // Act
//            sut.onCommand(playerMock, commandMock, "", arrayOf("COIN"))
//
//            // Assert
//            testScheduler.advanceUntilIdle()
//            verify(exactly = 1) {
//                playerMock.sendMessage(
//                    Component.text("Balance: ").append(Component.text("10 Coins")),
//                )
//            }
//        }
//    }
// }
