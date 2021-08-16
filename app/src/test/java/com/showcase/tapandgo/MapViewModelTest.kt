package com.showcase.tapandgo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.showcase.tapandgo.base.UiState
import com.showcase.tapandgo.common.MockResponseLoader
import com.showcase.tapandgo.common.TestCoroutineRule
import com.showcase.tapandgo.data.repository.BiclooRepository
import com.showcase.tapandgo.data.repository.dto.Station
import com.showcase.tapandgo.presentation.map.MapFragmentUiModel
import com.showcase.tapandgo.presentation.map.MapViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class MapViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val testCoroutineRule = TestCoroutineRule()

    lateinit var viewModel: MapViewModel

    @MockK
    lateinit var biclooRepository: BiclooRepository

    @MockK
    lateinit var observer: Observer<UiState>

    private val slot = slot<UiState>()
    private val list = arrayListOf<UiState>()

    @Before
    fun init() {
        MockKAnnotations.init(this)
        viewModel = MapViewModel(
            testCoroutineRule.testCoroutineDispatcher,
            biclooRepository
        )
        viewModel.uiState.observeForever(observer)
    }

    @After
    fun tearDown() {
        viewModel.uiState.removeObserver(observer)
    }

    @Test
    fun `test get bicloo response succeed`() {
        testCoroutineRule.runBlockingTest {
            val mockResponseLoader = MockResponseLoader.create<List<Station>>("biclooStations")
            val networkResponse = mockResponseLoader.model

            coEvery { biclooRepository.getStationsByContract() } returns flowOf(networkResponse)
            every { observer.onChanged(capture(slot)) } answers {
                list.add(slot.captured)
            }

            viewModel.retrieveBiclooLocations()

            val expectedUIStateLoading = UiState.Loading
            val expectedUIStateSuccess = UiState.Success(MapFragmentUiModel(networkResponse))

            Assert.assertEquals(list[0], expectedUIStateLoading)
            Assert.assertEquals(list[1], expectedUIStateSuccess)
        }
    }
}
