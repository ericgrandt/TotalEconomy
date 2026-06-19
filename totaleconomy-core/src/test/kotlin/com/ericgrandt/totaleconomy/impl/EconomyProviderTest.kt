package com.ericgrandt.totaleconomy.impl

// class EconomyProviderTest {
//    @MockK
//    lateinit var loggerMock: Logger
//
//    @MockK
//    lateinit var accountDataMock: AccountData
//
//    @MockK
//    lateinit var currencyDataMock: CurrencyData
//
//    private lateinit var sut: EconomyProvider
//
//    @BeforeTest
//    fun setUp() {
//        MockKAnnotations.init(this, relaxUnitFun = true)
//        mockTransaction()
//        sut = EconomyProvider(loggerMock, accountDataMock, currencyDataMock)
//    }
//
//    @Test
//    fun `getDefaultCurrency with success should return default currency`() {
//        // Arrange
//        val currency = TECurrency("USD", "Dollar", "Dollars", "$", 2, true)
//        every { currencyDataMock.getDefaultCurrency() } returns Ok(currency)
//
//        // Act
//        val actual = sut.getDefaultCurrency()
//
//        // Assert
//        assertEquals(currency, actual)
//    }
//
//    @Test
//    fun `getDefaultCurrency with NoSuchElementException should throw MissingDefaultCurrencyException`() {
//        // Arrange
//        every { currencyDataMock.getDefaultCurrency() } returns Err(NoSuchElementException())
//
//        // Act
//        assertThrows<MissingDefaultCurrencyException> {
//            sut.getDefaultCurrency()
//        }
//        verify {
//            loggerMock.error("default currency not found", any<NoSuchElementException>())
//        }
//    }
//
//    @Test
//    fun `getDefaultCurrency with SQLException should log and throw DatabaseException`() {
//        // Arrange
//        every { currencyDataMock.getDefaultCurrency() } returns Err(SQLException())
//
//        // Act
//        assertThrows<DatabaseException> {
//            sut.getDefaultCurrency()
//        }
//        verify {
//            loggerMock.error("database exception when getting default currency", any<SQLException>())
//        }
//    }
//
//    @Test
//    fun `getCurrency with success should return currency`() {
//        // Arrange
//        val currency = TECurrency("USD", "Dollar", "Dollars", "$", 2, true)
//        every { currencyDataMock.getCurrency("USD") } returns Ok(currency)
//
//        // Act
//        val actual = sut.getCurrency("USD")
//
//        // Assert
//        assertEquals(currency, actual)
//    }
//
//    @Test
//    fun `getCurrency with NoSuchElementException should log and throw CurrencyNotFoundException`() {
//        // Arrange
//        every { currencyDataMock.getCurrency("USD") } returns Err(NoSuchElementException())
//
//        // Act
//        assertThrows<CurrencyNotFoundException> {
//            sut.getCurrency("USD")
//        }
//        verify {
//            loggerMock.error("currency not found", any<NoSuchElementException>())
//        }
//    }
//
//    @Test
//    fun `getCurrency with SQLException should log and throw DatabaseException`() {
//        // Arrange
//        every { currencyDataMock.getCurrency("USD") } returns Err(SQLException())
//
//        // Act
//        assertThrows<DatabaseException> {
//            sut.getCurrency("USD")
//        }
//        verify {
//            loggerMock.error("database exception when getting currency", any<SQLException>())
//        }
//    }
//
//    @Test
//    fun `createAccount with success should return account`() {
//        // Arrange
//        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
//        every { accountDataMock.createAccount(any(), any(), any()) } returns Ok(account)
//
//        // Act
//        val actual = sut.createAccount(account.playerId, account.currencyCode)
//
//        // Assert
//        assertEquals(account, actual)
//    }
//
//    @Test
//    fun `createAccount with NoSuchElementException should log and throw AccountNotFoundException`() {
//        // Arrange
//        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
//        every { accountDataMock.createAccount(any(), any(), any()) } returns Err(NoSuchElementException())
//
//        // Act
//        assertThrows<AccountNotFoundException> {
//            sut.createAccount(account.playerId, account.currencyCode)
//        }
//        verify {
//            loggerMock.error("account not found after creation", any<NoSuchElementException>())
//        }
//    }
//
//    @Test
//    fun `createAccount with SQLException should log and throw DatabaseException`() {
//        // Arrange
//        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
//        every { accountDataMock.createAccount(any(), any(), any()) } returns Err(SQLException())
//
//        // Act/Assert
//        assertThrows<DatabaseException> {
//            sut.createAccount(account.playerId, account.currencyCode)
//        }
//        verify {
//            loggerMock.error("database exception when creating account", any<SQLException>())
//        }
//    }
//
//    @Test
//    fun `getAccount with success should return account`() {
//        // Arrange
//        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
//        every { accountDataMock.getAccount(any(), any()) } returns Ok(account)
//
//        // Act
//        val actual = sut.getAccount(account.playerId, account.currencyCode)
//
//        // Assert
//        assertEquals(account, actual)
//    }
//
//    @Test
//    fun `getAccount with NoSuchElementException should log and throw AccountNotFoundException`() {
//        // Arrange
//        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
//        every { accountDataMock.getAccount(any(), any()) } returns Err(NoSuchElementException())
//
//        // Act/Assert
//        assertThrows<AccountNotFoundException> {
//            sut.getAccount(account.playerId, account.currencyCode)
//        }
//        verify {
//            loggerMock.warn("account not found", any<NoSuchElementException>())
//        }
//    }
//
//    @Test
//    fun `getAccount with SQLException should log and throw DatabaseException`() {
//        // Arrange
//        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
//        every { accountDataMock.getAccount(any(), any()) } returns Err(SQLException())
//
//        // Act/Assert
//        assertThrows<DatabaseException> {
//            sut.getAccount(account.playerId, account.currencyCode)
//        }
//        verify {
//            loggerMock.error("database exception when getting account", any<SQLException>())
//        }
//    }
// }
