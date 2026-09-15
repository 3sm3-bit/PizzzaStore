package com.pizzza.pizzzastore.ui.login

import com.pizzza.pizzzastore.repository.db.entity.UserEntity
import com.pizzza.pizzzastore.repository.network.exception.UiTayApiException
import com.pizzza.pizzzastore.repository.network.model.LoginRequest
import com.pizzza.pizzzastore.repository.network.model.UserResponse
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import com.pizzza.pizzzastore.ui.base.GlobalUiStateManager
import com.pizzza.pizzzastore.usecases.DataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.collections.copy

class AuthViewModel(
    private val dataUseCase: DataUseCase,
    private val globalUiStateManager: GlobalUiStateManager
) : BaseViewModel() {

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    fun onUserChange(newUser: String) {
        _authUiState.update { it.copy(user = newUser) }
    }

    fun onPassChange(newPass: String) {
        _authUiState.update { it.copy(pass = newPass) }
    }

    fun selectUser(user: UserResponse?) {
        _authUiState.update { state ->
            if (user == null) {
                AuthUiState().copy(user = state.user, pass = state.pass)
            } else {
                state.copy(
                    selectedUser = user,
                    nameUser = user.nameUser ?: "",
                    names = user.names ?: "",
                    lastName = user.lastName ?: "",
                    document = user.document ?: "11111111",
                    email = user.email ?: "",
                    phone = user.phone?.replace("+52", "") ?: "",
                    address = user.address ?: "",
                    rol = user.rol ?: "CLIENTE",
                    area = user.area ?: "1",
                    longitude = user.longitude ?: "",
                    latitude = user.latitude ?: "",
                    pass = "********" // Placeholder for edit
                )
            }
        }
    }


    fun login(onSuccess: () -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            val request = LoginRequest(
                nameUser = _authUiState.value.user,
                password = _authUiState.value.pass
            )
            val response = io { dataUseCase.login(request) }
            val userValid = response.userValid
            val userRole = userValid.rol?.uppercase() ?: ""
            if (userRole != "STORE" && userRole != "ADMIN") {
                throw UiTayApiException(
                    code = 401,
                    title = "Usuario no autorizado",
                    messageApi = "El usuario ingresado no está autorizado para esta aplicación"
                )
            }

            val userEntity = UserEntity(
                uid = userValid.uid ?: "",
                nameUser = userValid.nameUser ?: "",
                names = userValid.names ?: "",
                lastName = userValid.lastName ?: "",
                document = userValid.document ?: "",
                email = userValid.email ?: "",
                phone = userValid.phone ?: "",
                address = userValid.address ?: "",
                rol = userValid.rol ?: "CLIENTE",
                area = userValid.area ?: "1",
                longitude = userValid.longitude ?: "",
                latitude = userValid.latitude ?: "",
                token = response.token
            )

            io { dataUseCase.saveUserLocal(userEntity) }

            _authUiState.update { it.copy(isLoginSuccessful = true) }
            onSuccess()
        }
    }

    fun onRegisterFieldChange(
        nameUser: String = _authUiState.value.nameUser,
        names: String = _authUiState.value.names,
        lastName: String = _authUiState.value.lastName,
        document: String = _authUiState.value.document,
        email: String = _authUiState.value.email,
        phone: String = _authUiState.value.phone,
        address: String = _authUiState.value.address,
        area: String = _authUiState.value.area,
        longitude: String = _authUiState.value.longitude,
        latitude: String = _authUiState.value.latitude,
        pass: String = _authUiState.value.pass,
        rol: String = _authUiState.value.rol
    ) {
        _authUiState.update {
            it.copy(
                nameUser = nameUser,
                names = names,
                lastName = lastName,
                document = document,
                email = email,
                phone = phone,
                address = address,
                area = area,
                longitude = longitude,
                latitude = latitude,
                pass = pass,
                rol = rol
            )
        }
    }
    fun register(onSuccess: (String) -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            val state = _authUiState.value
            val request = UserResponse(
                nameUser = state.nameUser,
                names = state.names,
                lastName = state.lastName,
                document = state.document,
                email = state.email,
                password = if (state.pass == "********") null else state.pass,
                phone = if (state.phone.startsWith("+52")) state.phone else "+52${state.phone}",
                address = state.address,
                rol = state.rol,
                area = state.area,
                longitude = state.longitude,
                latitude = state.latitude,
                uid = state.selectedUser?.uid
            )
            
            val response = if (state.isEditMode) {
                io { dataUseCase.updateUser(request) }
            } else {
                io { dataUseCase.registerUser(request) }
            }
            onSuccess(response)
        }
    }

}