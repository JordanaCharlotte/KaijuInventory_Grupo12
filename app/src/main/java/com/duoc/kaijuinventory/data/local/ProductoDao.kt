package com.duoc.kaijuinventory.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY id")
    suspend fun obtenerTodos(): List<ProductoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodos(productos: List<ProductoEntity>)

    @Query("DELETE FROM productos")
    suspend fun eliminarTodos()
}
