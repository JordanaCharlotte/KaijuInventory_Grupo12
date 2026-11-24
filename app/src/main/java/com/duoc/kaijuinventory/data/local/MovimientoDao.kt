package com.duoc.kaijuinventory.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovimientoDao {

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    suspend fun obtenerTodos(): List<MovimientoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodos(movimientos: List<MovimientoEntity>)

    @Query("DELETE FROM movimientos")
    suspend fun eliminarTodos()
}
