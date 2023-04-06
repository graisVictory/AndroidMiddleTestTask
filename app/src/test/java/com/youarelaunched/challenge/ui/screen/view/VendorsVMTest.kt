package com.youarelaunched.challenge.ui.screen.view

import com.youarelaunched.challenge.data.repository.VendorsRepository
import com.youarelaunched.challenge.data.repository.model.Vendor
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class VendorsVMTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test get vendors success loading updates state`() = runTest {
        val repo = mockk<VendorsRepository>()
        coEvery { repo.getVendors(any()) } returns listOf(
            Vendor(
                id = 1,
                companyName = "Test name",
                coverPhoto = "smth",
                area = "Test",
                favorite = false,
                categories = null,
                tags = null
            )
        )

        val vm = VendorsVM(repo)

        // Droping 1st default value
        val state = vm.uiState.drop(1).first()
        assert(
            state.vendors == listOf(
                Vendor(
                    id = 1,
                    companyName = "Test name",
                    coverPhoto = "smth",
                    area = "Test",
                    favorite = false,
                    categories = null,
                    tags = null
                )
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test get vendors error `() = runTest {
        val repo = mockk<VendorsRepository>()
        coEvery { repo.getVendors(any()) } throws java.lang.IllegalArgumentException("smth")

        val vm = VendorsVM(repo)

        val result = vm.uiState.drop(1).first()
        assert(result.error == "smth")
    }
}
