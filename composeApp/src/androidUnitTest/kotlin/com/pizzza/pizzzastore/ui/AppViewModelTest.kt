package com.pizzza.pizzzastore.ui

import android.util.Log
import com.pizzza.pizzzastore.model.ParentOrderModel
import com.pizzza.pizzzastore.usecases.DataUseCase
import com.pizzza.pizzzastore.utils.DispatcherProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    // Implementación de DispatcherProvider para pruebas
    private val testDispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
    }

    private lateinit var dataUseCase: DataUseCase
    private lateinit var viewModel: AppViewModel

    @BeforeTest
    fun setUp() {
        // Mockeamos la clase Log de Android para que no falle en el entorno JVM
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        
        // Configuramos el despachador Main para que viewModelScope lo use
        Dispatchers.setMain(testDispatcher)
        
        dataUseCase = mockk()
        // Inyectamos el UseCase y nuestros Dispatchers de prueba
        viewModel = AppViewModel(dataUseCase, testDispatchers)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `obtener lista de pedidos actualiza el estado correctamente`() = runTest {
        // PREPARACIÓN
        val mockOrders = listOf(
            ParentOrderModel(uid = "1", nameClient = "Tayler", state = "CONFIRMADO", orders = emptyList(), description = "", phone = "", price = "100", date = "", address = "", reception = ""),
            ParentOrderModel(uid = "2", nameClient = "Juan", state = "LISTO", orders = emptyList(), description = "", phone = "", price = "200", date = "", address = "", reception = "")
        )
        coEvery { dataUseCase.loadParentOrder(any()) } returns mockOrders

        // ACCIÓN
        viewModel.getGeneralOrderList()
        testDispatcher.scheduler.advanceUntilIdle()

        // VERIFICACIÓN
        val uiState = viewModel.orderUiState
        assertEquals(2, uiState.orders.size)
        assertEquals(1, uiState.countConfirmado)
        assertEquals(1, uiState.countListo)
    }

    @Test
    fun `aplicar filtro actualiza la lista filtrada`() = runTest {
        // PREPARACIÓN
        val mockOrders = listOf(
            ParentOrderModel(uid = "1", nameClient = "Tayler", state = "CONFIRMADO", orders = emptyList(), description = "", phone = "", price = "100", date = "", address = "", reception = ""),
            ParentOrderModel(uid = "2", nameClient = "Juan", state = "LISTO", orders = emptyList(), description = "", phone = "", price = "200", date = "", address = "", reception = "")
        )
        coEvery { dataUseCase.loadParentOrder(any()) } returns mockOrders
        
        viewModel.getGeneralOrderList()
        testDispatcher.scheduler.advanceUntilIdle()

        // ACCIÓN
        viewModel.applyFilter("LISTO")

        // VERIFICACIÓN
        val uiState = viewModel.orderUiState
        assertEquals(1, uiState.filteredOrders.size)
        assertEquals("LISTO", uiState.filteredOrders[0].state)
    }
}
