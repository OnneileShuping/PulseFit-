package com.example.pulsefit

import com.example.pulsefit.data.repository.PulseFitRepository
import com.example.pulsefit.ui.logger.ActivityViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: PulseFitRepository
    private lateinit var viewModel: ActivityViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = mockk(relaxed = true)
        viewModel = ActivityViewModel(repo, "user-123")
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `save activity calls repository and updates saved flag`() = runTest {
        coEvery { repo.saveActivity(any(), any()) } returns Unit

        viewModel.save("Running", 1920, 5.0, 320, "morning run")
        advanceUntilIdle()

        assertTrue(viewModel.saved.value)
        coVerify(exactly = 1) { repo.saveActivity("user-123", any()) }
    }
}