package com.duoc.kaijuinventory.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.duoc.kaijuinventory.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore separado para recordar la sesión al cerrar y abrir la app
private val Context.sesionDataStore: DataStore<Preferences> by preferencesDataStore(name = "sesion_kaiju")

class SesionDataStore(private val context: Context) {

    private val claveNombre = stringPreferencesKey("nombre")
    private val claveEmail = stringPreferencesKey("email")
    private val claveRol = stringPreferencesKey("rol")

    // null = no hay sesión iniciada
    val usuario: Flow<Usuario?> = context.sesionDataStore.data.map { prefs ->
        val email = prefs[claveEmail] ?: return@map null
        Usuario(
            nombre = prefs[claveNombre] ?: "",
            email = email,
            rol = prefs[claveRol] ?: ""
        )
    }

    suspend fun guardar(usuario: Usuario) {
        context.sesionDataStore.edit { prefs ->
            prefs[claveNombre] = usuario.nombre
            prefs[claveEmail] = usuario.email
            prefs[claveRol] = usuario.rol
        }
    }

    suspend fun cerrar() {
        context.sesionDataStore.edit { it.clear() }
    }
}
