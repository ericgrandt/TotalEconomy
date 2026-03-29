package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.commands.BalanceCommandExecutor
import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.BalanceData
import com.ericgrandt.totaleconomy.data.Database
import com.ericgrandt.totaleconomy.econ.CommonEconomy
import com.ericgrandt.totaleconomy.testutils.TestUtils
import com.zaxxer.hikari.HikariDataSource
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.entity.Player
import kotlin.emptyArray
import kotlin.test.BeforeTest

class BalanceCommandExecutorTest {
    @MockK
    lateinit var databaseMock: Database

    @MockK
    lateinit var playerMock : Player

    @MockK
    lateinit var commandMock : Command

    @BeforeTest
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    @Tag("Integration")
    fun onCommand_ShouldSendMessageWithBalanceToPlayer() = runTest {
        // Arrange
        TestUtils.resetDb()
        val account = TestUtils.seedAccount()
        TestUtils.seedBalance(account.id, null)

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()
        every { playerMock.uniqueId } returns account.id

        val economy = CommonEconomy(AccountData(databaseMock), BalanceData(databaseMock))
        val balanceCommand = BalanceCommand(economy)

        val sut = BalanceCommandExecutor(this, balanceCommand)

        // Act
        sut.onCommand(playerMock, commandMock, "", emptyArray())

        // Assert
        testScheduler.advanceUntilIdle()

        verify(exactly = 1) { playerMock.sendMessage(Component.text("You have ").append(Component.text("1.23 Diamonds"))) }
    }
}