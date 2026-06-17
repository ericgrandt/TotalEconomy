package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.CurrencyData
import com.ericgrandt.totaleconomy.economy.EconomyProvider
import com.ericgrandt.totaleconomy.testutils.TestUtils
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import kotlin.test.BeforeTest
import org.bukkit.entity.Player as PlayerEntity

class BalanceCommandExecutorTest {
    @MockK
    lateinit var loggerMock: Logger

    @MockK
    lateinit var playerMock: PlayerEntity

    @MockK
    lateinit var commandMock: Command

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    @Tag("Integration")
    fun `onCommand with no currency code argument should send default balance to player`() {
        runTest {
            // Arrange
            TestUtils.connectToTestDb()
            val currency = TestUtils.seedDefaultCurrency()
            val account = TestUtils.seedAccount(currency.code)

            every { playerMock.uniqueId } returns account.playerId
            every { playerMock.name } returns "TestName"

            val economy = EconomyProvider(loggerMock, AccountData(), CurrencyData())
            val balanceCommand = BalanceCommand(economy, StandardTestDispatcher(testScheduler))

            val sut = BalanceCommandExecutor(this, balanceCommand)

            // Act
            sut.onCommand(playerMock, commandMock, "", emptyArray())

            // Assert
            testScheduler.advanceUntilIdle()
            verify(exactly = 1) {
                playerMock.sendMessage(
                    Component.text("Balance: ").append(Component.text("$10.00")),
                )
            }
        }
    }

    @Test
    @Tag("Integration")
    fun `onCommand with currency code argument should send currency balance to player`() {
        runTest {
            // Arrange
            TestUtils.connectToTestDb()
            val defaultCurrency = TestUtils.seedDefaultCurrency()
            val currency = TestUtils.seedNonDefaultCurrency()
            val defaultAccount = TestUtils.seedAccount(defaultCurrency.code)
            TestUtils.seedAccount(currency.code)

            every { playerMock.uniqueId } returns defaultAccount.playerId
            every { playerMock.name } returns "TestName"

            val economy = EconomyProvider(loggerMock, AccountData(), CurrencyData())
            val balanceCommand = BalanceCommand(economy, StandardTestDispatcher(testScheduler))

            val sut = BalanceCommandExecutor(this, balanceCommand)

            // Act
            sut.onCommand(playerMock, commandMock, "", arrayOf("COIN"))

            // Assert
            testScheduler.advanceUntilIdle()
            verify(exactly = 1) {
                playerMock.sendMessage(
                    Component.text("Balance: ").append(Component.text("10 Coins")),
                )
            }
        }
    }
}
