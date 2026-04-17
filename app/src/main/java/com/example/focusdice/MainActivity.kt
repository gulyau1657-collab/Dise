package com.example.focusdice

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.focusdice.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storage: SessionStorage

    private val tasks = listOf(
        FocusTask("Глубокая работа", "25 минут без отвлечений"),
        FocusTask("Разбор почты", "10 минут и только важное"),
        FocusTask("Порядок вокруг", "Убери рабочее место за 7 минут"),
        FocusTask("Микро-движение", "20 приседаний или короткая прогулка"),
        FocusTask("Вода и пауза", "Стакан воды и 3 спокойных вдоха"),
        FocusTask("План на час", "Запиши 3 главные задачи"),
        FocusTask("Чтение", "Прочитай 5 страниц полезного текста"),
        FocusTask("Фокус-спринт", "15 минут на одну неприятную задачу")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = SessionStorage(this)
        renderStats()
        renderTask(null)

        binding.rollButton.setOnClickListener {
            val task = tasks.random(Random(System.currentTimeMillis()))
            renderTask(task)
        }

        binding.doneButton.setOnClickListener {
            storage.incrementCompleted()
            renderStats()
            binding.statusText.text = getString(R.string.done_message)
        }

        binding.resetButton.setOnClickListener {
            storage.reset()
            renderStats()
            binding.statusText.text = getString(R.string.reset_message)
            renderTask(null)
        }
    }

    private fun renderTask(task: FocusTask?) {
        if (task == null) {
            binding.taskTitle.text = getString(R.string.tap_to_start)
            binding.taskDescription.text = getString(R.string.task_hint)
            return
        }
        binding.taskTitle.text = task.title
        binding.taskDescription.text = task.description
        binding.statusText.text = getString(R.string.roll_message)
        storage.incrementRolls()
        renderStats()
    }

    private fun renderStats() {
        binding.rollsCount.text = storage.getRolls().toString()
        binding.doneCount.text = storage.getCompleted().toString()
    }
}

data class FocusTask(
    val title: String,
    val description: String
)

class SessionStorage(context: Context) {
    private val prefs = context.getSharedPreferences("focus_dice", Context.MODE_PRIVATE)

    fun getRolls(): Int = prefs.getInt("rolls", 0)
    fun getCompleted(): Int = prefs.getInt("completed", 0)

    fun incrementRolls() {
        prefs.edit().putInt("rolls", getRolls() + 1).apply()
    }

    fun incrementCompleted() {
        prefs.edit().putInt("completed", getCompleted() + 1).apply()
    }

    fun reset() {
        prefs.edit().clear().apply()
    }
}
