package com.spendly.backend

import com.spendly.backend.dto.RegisterRequest
import com.spendly.backend.entity.User
import com.spendly.backend.repository.UserRepository
import com.spendly.backend.service.IEmailService
import com.spendly.backend.service.impl.UserServiceImpl
import com.spendly.backend.types.Role
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@SpringBootTest
class UserServiceTest {

    private lateinit var userService: UserServiceImpl
    private val userRepository: UserRepository = mockk()
    private val emailService: IEmailService = mockk()
    private val passwordEncoder: BCryptPasswordEncoder = mockk()

    @BeforeEach
    fun setup() {
        userService = UserServiceImpl(userRepository, emailService)
        every { passwordEncoder.encode(any()) } answers { "hashedPassword" }
    }

    @Test
    fun `userDetailsService should return user if found`() {
        val user = User("testUser", "prashan", "password", "test@example.com", Role.USER, false)
        every { userRepository.findUserByEmail("test@example.com") } returns user

        val userDetailsService: UserDetailsService = userService.userDetailsService()
        val loadedUser = userDetailsService.loadUserByUsername("test@example.com")

        assertNotNull(loadedUser)
        assertEquals("test@example.com", loadedUser.username)
    }

    @Test
    fun `userDetailsService should throw exception if user not found`() {
        every { userRepository.findUserByEmail("nonexistent@example.com") } returns null

        val userDetailsService: UserDetailsService = userService.userDetailsService()

        assertNull(userDetailsService.loadUserByUsername("nonexistent@example.com"))
    }

    @Test
    fun `findUserByEmail should return user if found`() {
        val user = User("testUser", "test@example.com", "password", "test@example.com", Role.USER, false)
        every { userRepository.findUserByEmail("test@example.com") } returns user

        val foundUser = userService.findUserByEmail("test@example.com")

        assertNotNull(foundUser)
        assertEquals("test@example.com", foundUser?.email)
    }

    @Test
    fun `findUserByEmail should return null if user not found`() {
        every { userRepository.findUserByEmail("nonexistent@example.com") } returns null

        val foundUser = userService.findUserByEmail("nonexistent@example.com")

        assertNull(foundUser)
    }

    @Test
    fun `registerUser should save new user if email is not taken`() {
        val request = RegisterRequest("testUser", "test@example.com", "password",  Role.USER)
        every { userRepository.findUserByEmail("test@example.com") } returns null
        every { passwordEncoder.encode("password") } returns "hashedPassword"
        every { userRepository.save(any()) } answers { firstArg() }
        every { emailService.sendVerificationEmail(any()) } just Runs

        val registeredUser = userService.registerUser(request)

        assertNotNull(registeredUser)
        assertEquals("test@example.com", registeredUser.email)
        assertFalse(registeredUser.isEmailVerified)
        verify { emailService.sendVerificationEmail(registeredUser) }
    }

    @Test
    fun `registerUser should throw exception if email is already registered`() {
        val request = RegisterRequest("testUser", "test@example.com", "password", Role.USER)
        val existingUser = User("testUser", "test@example.com", "password", "test@example.com",  Role.USER, true)
        every { userRepository.findUserByEmail("test@example.com") } returns existingUser

        assertThrows<IllegalArgumentException> {
            userService.registerUser(request)
        }
    }
}
