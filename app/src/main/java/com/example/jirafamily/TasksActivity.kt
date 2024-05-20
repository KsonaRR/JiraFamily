import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.adapters.TaskAdapter
import com.example.jirafamily.DTO.Task
import com.example.jirafamily.R

class TasksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        // Создание списка задач
//        val tasks = mutableListOf(
//            Task(
//                title = "Название задачи",
//                description = "Описание задачи",
//                avatarUrl = null,
//                attachmentUrl = null,
//                status = "В процессе",
//                priority = "Высокий"
//            )
//        )
//
//
//
//        // Настройка RecyclerView
//        val recyclerView: RecyclerView = findViewById(R.id.taskRecycleView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        // Создание и установка адаптера
//        val adapter = TaskAdapter(tasks)
//        recyclerView.adapter = adapter
    }
}
