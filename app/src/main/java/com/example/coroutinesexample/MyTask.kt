package com.example.coroutinesexample

import com.example.coroutinesexample.databinding.ActivityMainBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class MyTask(val actividad: MainActivity, val nombre: String, val tiempo: Double) {

    suspend fun execute(count : Int) = withContext(Dispatchers.IO){
        try {
            var index = 0
            while (index < count) {
                Thread.sleep((1000 * tiempo).toLong())
                actividad.actualizacion(index, nombre)
                index++
            }
        }
        catch (e:CancellationException){
            actividad.finTarea(e.message!!, nombre)
        }
        finally {
            if (isActive){
                actividad.finTarea("Tarea finalizada", nombre)
            }
        }
    }


}