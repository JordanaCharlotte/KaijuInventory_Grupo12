package com.duoc.kaijuinventory.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.kaijuinventory.data.SesionDataStore
import com.duoc.kaijuinventory.data.remote.BackendCliente
import com.duoc.kaijuinventory.data.remote.LoginRequestDto
import com.duoc.kaijuinventory.data.remote.mensajeDeError
import com.duoc.kaijuinventory.model.LoginFormState
import com.duoc.kaijuinventory.model.Usuario
import com.duoc.kaijuinventory.util.validarCorreo
import com.duoc.kaijuinventory.util.validarPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// AndroidViewModel porque la sesión se guarda en DataStore, que necesita el contexto
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val sesion = SesionDataStore(application)
    private val usuariosApi = BackendCliente.usuariosApi

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    // Usuario con sesión iniciada (null si no hay sesión)
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    // false mientras se lee la sesión guardada al abrir la app
    private val _sesionCargada = MutableStateFlow(false)
    val sesionCargada: StateFlow<Boolean> = _sesionCargada.asStateFlow()

    init {
        viewModelScope.launch {
            _usuario.value = sesion.usuario.first()
            _sesionCargada.value = true
        }
    }

    fun onEmailChange(value: String) {
        _formState.update { it.copy(email = value, emailError = null, errorServidor = null) }
    }

    fun onPasswordChange(value: String) {
        _formState.update { it.copy(password = value, passwordError = null, errorServidor = null) }
    }

    // 1) valida los campos, 2) consulta a ms-usuarios, 3) guarda la sesión
    fun iniciarSesion(alIngresar: () -> Unit) {
        val estado = _formState.value
        val emailError = validarCorreo(estado.email)
        val passwordError = validarPassword(estado.password)

        _formState.update { it.copy(emailError = emailError, passwordError = passwordError) }
        if (emailError != null || passwordError != null) return

        viewModelScope.launch {
            _formState.update { it.copy(cargando = true, errorServidor = null) }
            try {
                val respuesta = usuariosApi.login(LoginRequestDto(estado.email.trim(), estado.password))
                val usuario = Usuario(respuesta.nombre, respuesta.email, respuesta.rol)
                sesion.guardar(usuario)
                _usuario.value = usuario
                _formState.value = LoginFormState() // limpia el formulario
                alIngresar()
            } catch (e: IOException) {
                _formState.update { it.copy(cargando = false, errorServidor = mensajeDeError(e)) }
            } catch (e: HttpException) {
                _formState.update { it.copy(cargando = false, errorServidor = mensajeDeError(e)) }
            }
        }
    }

    fun cerrarSesion(alSalir: () -> Unit) {
        viewModelScope.launch {
            sesion.cerrar()
            _usuario.value = null
            alSalir()
        }
    }
}
