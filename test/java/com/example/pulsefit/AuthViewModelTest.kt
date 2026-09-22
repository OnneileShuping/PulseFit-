package com.example.pulsefit

import com.example.pulsefit.data.repository.AuthRepository
import com.example.pulsefit.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = mockk(relaxed = true)
        viewModel = AuthViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with empty fields sets error`() = runTest {
        viewModel.login("", "")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoggedIn)
        assertEquals("Fields cannot be empty", state.error)
    }

    @Test
    fun `login with valid credentials sets isLoggedIn`() = runTest {
        val fakeUser = mockk<FirebaseUser>(relaxed = true)
        coEvery { repo.login("alex@test.com", "secret1") } returns Result.success(fakeUser)

        viewModel.login("alex@test.com", "secret1")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isLoggedIn)
        assertNull(state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `login failure surfaces error message`() = runTest {
        coEvery { repo.login(any(), any()) } returns
                Result.failure(Exception("Invalid email or password"))

        viewModel.login("bad@test.com", "wrong")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoggedIn)
        assertEquals("Invalid email or password", state.error)
    }

    @Test
    fun `register with short password sets error`() = runTest {
        viewModel.register("a@b.com", "alex", "123")
        advanceUntilIdle()

        assertEquals(
            "Password must be at least 6 characters",
            viewModel.state.value.error
        )
    }

    @Test
    fun `register success sets isLoggedIn true`() = runTest {
        val fakeUser = mockk<FirebaseUser>(relaxed = true)
        coEvery { repo.register(any(), any(), any()) } returns Result.success(fakeUser)

        viewModel.register("alex@test.com", "Alex", "secret1")
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isLoggedIn)
    }

    @Test
    fun `logout clears session`() = runTest {
        viewModel.logout()
        assertFalse(viewModel.state.value.isLoggedIn)
    }
}