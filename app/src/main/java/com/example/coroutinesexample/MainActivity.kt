package com.example.coroutinesexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.example.coroutinesexample.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var stringBuilder= StringBuilder()
    private val parametroTarea1 = 5
    private val parametroTarea2 = 10
    private lateinit var task1:MyTask
    private lateinit var task2:MyTask
    private lateinit var job1:Job
    private lateinit var job2:Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        binding.statusText?.movementMethod= ScrollingMovementMethod()
        stringBuilder = StringBuilder("Empezando actividad\n")
        stringBuilder.append("Esperando click.\n")
        binding.statusText.text = "${stringBuilder.toString()}"

        binding.progressBar.max = parametroTarea1 + parametroTarea2

        task1 = MyTask(this, "Tarea 1", 1.0)
        task2 = MyTask(this, "Tarea 2", 0.5)

        binding.btnAsync.setOnClickListener {
            binding.btnCancel.isEnabled = true
            binding.btnAsync.isEnabled = false
            startTasks()
        }

        binding.btnCancel.setOnClickListener {
            binding.btnCancel.isEnabled = false
            binding.btnAsync.isEnabled = true
            cancelTask()
        }
    }

    fun startTasks(){
        job1 = MainScope().launch {
            executeDesdeActividad(parametroTarea1, 1.0, "Tarea1 desde Actividad")
            task1.execute(parametroTarea1)
        }
        job2 = MainScope().launch {
            task2.execute(parametroTarea2)
            executeDesdeActividad(parametroTarea2, 0.5, "Tarea2 desde Actividad")
        }
    }

    fun cancelTask(){
        MainScope().launch {
            job1.cancel(CancellationException("Tarea cancelada"))
            //finTarea("Tarea cancelada", "Tarea 1")
            job2.cancel(CancellationException("Tarea cancelada"))
            //finTarea("Tarea cancelada", "Tarea 2")
        }
    }

    suspend fun actualizacion(valor:Int, nombre:String) = withContext(Dispatchers.Main){
        stringBuilder.append("Tarea: ${nombre}. Tratando el parametro: ${valor}\n")
        binding.statusText.text = "${stringBuilder.toString()}"
        binding.progressBar.progress = binding.progressBar.progress + 1
    }

    suspend fun finTarea(mensaje:String, nombre: String) = withContext(Dispatchers.Main){
        stringBuilder.append("Tarea: ${nombre}.  ${mensaje}\n")
        binding.statusText.text = "${stringBuilder.toString()}"
        binding.progressBar.progress = 0
    }


    suspend fun executeDesdeActividad(count : Int, tiempo:Double, nombre:String) = withContext(Dispatchers.IO){
        try {
            var index = 0
            while (index < count) {
                Thread.sleep((1000 * tiempo).toLong())
                actualizacion(index, nombre)
                index++
            }
        }
        catch (e:CancellationException){
            finTarea(e.message!!, nombre)
        }
        finally {
            if (isActive){
                finTarea("Tarea finalizada", nombre)
            }
        }
    }
}