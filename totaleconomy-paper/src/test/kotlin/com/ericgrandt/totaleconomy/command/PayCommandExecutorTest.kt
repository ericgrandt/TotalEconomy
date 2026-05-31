package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.BalanceData
import com.ericgrandt.totaleconomy.econ.CommonEconomy
import com.ericgrandt.totaleconomy.testutils.TestUtils
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

class PayCommandExecutorTest {
    @MockK
    lateinit var fromPlayerMock: Player

    @MockK
    lateinit var toPlayerMock: Player

    @MockK
    lateinit var commandMock: Command

    @BeforeTest
    fun setup() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    @Tag("Integration")
    fun onCommand_ShouldSendMessagesToInvolvedPlayers() {
        runTest {
            // Arrange
            TestUtils.connectToTestDb()
            val fromAccount = TestUtils.seedAccount()
            val toAccount = TestUtils.seedAccount()
            TestUtils.seedBalance(fromAccount.id, null)
            TestUtils.seedBalance(toAccount.id, null)

            every { fromPlayerMock.uniqueId } returns fromAccount.id
            every { toPlayerMock.uniqueId } returns toAccount.id
            every { fromPlayerMock.name } returns "fromPlayer"
            every { toPlayerMock.name } returns "toPlayer"

            mockkStatic(Bukkit::class)
            every { Bukkit.getPlayer("toPlayer") } returns toPlayerMock

            val economy = CommonEconomy(AccountData(), BalanceData())
            val payCommand = PayCommand(economy)

            val sut = PayCommandExecutor(this, payCommand)

            // Act
            sut.onCommand(fromPlayerMock, commandMock, "", arrayOf("toPlayer", "1.23"))

            // Assert
            testScheduler.advanceUntilIdle()

            verify(exactly = 1) {
                fromPlayerMock.sendMessage(
                    Component.text("You paid toPlayer ").append(Component.text("1.23 Diamonds")),
                )
                toPlayerMock.sendMessage(
                    Component
                        .text("You received ")
                        .append(Component.text("1.23 Diamonds"))
                        .append(Component.text(" from fromPlayer")),
                )
            }
        }
    }
}
