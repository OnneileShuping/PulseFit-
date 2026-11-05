package com.example.pulsefit

import com.example.pulsefit.data.repository.AuthRepository
import com.example.pulsefit.data.repository.PulseFitRepository
import com.example.pulsefit.ui.settings.SettingsViewModel
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
class SettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: PulseFitRepository
    private lateinit var authRepo: AuthRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = mockk(relaxed = true)
        authRepo = mockk(relaxed = true)
        viewModel = SettingsViewModel(repo, authRepo, "user-123")
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `changePassword rejects mismatched passwords`() = runTest {
        viewModel.changePassword("old", "newpass", "different")
        advanceUntilIdle()

        val state = viewModel.actionState.value
        assertTrue(state is SettingsViewModel.ActionState.Error)
        assertEquals("Passwords do not match", (state as SettingsViewModel.ActionState.Error).message)
    }

    @Test
    fun `changePassword rejects short new password`() = runTest {
        viewModel.changePassword("old", "123", "123")
        advanceUntilIdle()

        val state = viewModel.actionState.value
        assertTrue(state is SettingsViewModel.ActionState.Error)
    }

    @Test
    fun `changePassword success produces success state`() = runTest {
        coEvery { authRepo.changePassword(any(), any()) } returns Result.success(Unit)

        viewModel.changePassword("oldpass", "newpass1", "newpass1")
        advanceUntilIdle()

        assertTrue(viewModel.actionState.value is SettingsViewModel.ActionState.Success)
    }
}