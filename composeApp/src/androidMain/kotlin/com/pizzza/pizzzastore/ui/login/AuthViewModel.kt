package com.pizzza.pizzzastore.ui.login

import com.pizzza.pizzzastore.repository.db.entity.UserEntity
import com.pizzza.pizzzastore.repository.network.exception.UiTayApiException
import com.pizzza.pizzzastore.repository.network.model.LoginRequest
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import com.pizzza.pizzzastore.ui.base.GlobalUiStateManager
import com.pizzza.pizzzastore.usecases.DataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(
    private val dataUseCase: DataUseCase,
    private val globalUiStateManager: GlobalUiStateManager,
) : BaseViewModel() {

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    fun onUserChange(newUser: String) {
        _authUiState.update { it.copy(user = newUser) }
    }

    fun onPassChange(newPass: String) {
        _authUiState.update { it.copy(pass = newPass) }
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

}