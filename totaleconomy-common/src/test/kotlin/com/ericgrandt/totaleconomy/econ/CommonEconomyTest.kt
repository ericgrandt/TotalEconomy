package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.model.ResultA
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CommonEconomyTest {
    @MockK
    lateinit var accountDataMock: AccountData

    @BeforeTest
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    //@Test
    //fun createAccount_WithSuccessResultFromAccountData_ShouldReturnTrue() {
    //    // Arrange
    //    every { accountDataMock.createAccount(any()) } returns ResultA.Success<Boolean>(true)

    //    val sut = CommonEconomy(accountDataMock)

    //    // Act
    //    val actual = sut.createAccount(UUID.randomUUID())
    //    val expected = ResultA.Success<Boolean>(true)

    //    // Assert
    //    assertEquals(expected, actual)
    //}

    //@Test
    //fun createAccount_WithErrorResultFromAccountData_ShouldReturnAnErrorResult() {
    //    // Arrange
    //    every { accountDataMock.createAccount(any()) } returns ResultA.Error<Boolean>("", null)

    //    val sut = CommonEconomy(accountDataMock)

    //    // Act
    //    val actual = sut.createAccount(UUID.randomUUID())
    //    val expected = ResultA.Error<Boolean>("unable to create an account", null)

    //    // Assert
    //    assertEquals(expected, actual)
    //}

    //@Test
    //fun createAccount_WithInfoResultFromAccountData_ShouldReturnAnErrorResult() {
    //    // Arrange
    //    every { accountDataMock.createAccount(any()) } returns ResultA.Info<Boolean>("this should never happen")

    //    val sut = CommonEconomy(accountDataMock)

    //    // Act
    //    val actual = sut.createAccount(UUID.randomUUID())
    //    val expected = ResultA.Error<Boolean>("unexpected result", null)

    //    // Assert
    //    assertEquals(expected, actual)
    //}

    //@Test
    //fun hasAccount_WithAnAccount_ShouldReturnTrue() {

    //}

    //@Test
    //fun hasAccount_WithNoAccount_ShouldReturnAnInfoResult() {

    //}

    //@Test
    //fun hasAccount_WithNoAccount_ShouldReturnAnErrorResult() {

    //}
}