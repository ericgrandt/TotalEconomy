package com.ericgrandt.totaleconomy.impl

// class PaperPlayerTest {
//    @MockK
//    lateinit var playerMock: PlayerEntity
//
//    val uuid: UUID = UUID.randomUUID()
//
//    @BeforeTest
//    fun setUp() {
//        MockKAnnotations.init(this, relaxUnitFun = true)
//        every { playerMock.name } returns "PlayerName"
//        every { playerMock.uniqueId } returns uuid
//    }
//
//    @Test
//    fun getUniqueId_ShouldReturnThePlayersUUID() {
//        // Arrange
//        val sut = PaperPlayer(playerMock)
//
//        // Act
//        val actual = sut.uniqueId
//
//        // Assert
//        assertEquals(uuid, actual)
//    }
//
//    @Test
//    fun getName_ShouldReturnThePlayersName() {
//        // Arrange
//        val sut = PaperPlayer(playerMock)
//
//        // Act
//        val actual = sut.name
//
//        // Assert
//        assertEquals("PlayerName", actual)
//    }
//
//    @Test
//    fun sendMessage_ShouldCallSendMessage() {
//        // Arrange
//        val sut = PaperPlayer(playerMock)
//
//        // Act
//        sut.sendMessage(Component.text("Test"))
//
//        // Assert
//        verify(exactly = 1) {
//            playerMock.sendMessage(Component.text("Test"))
//        }
//    }
// }
