import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Task
import com.example.jirafamily.R

class TaskAdapter(private val tasks: ArrayList<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.taskNameTextView.text = currentTask.title
        holder.importanceTextView.text = "Важность:"
        // Установка изображения приоритета
        holder.taskPriorityImageView.setImageResource(getPriorityImage(currentTask.priority!!))

        // Установка изображения аватарки задачи (если есть ссылка)
        currentTask.avatarUrl?.let {
        }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        val importanceTextView: TextView = itemView.findViewById(R.id.importanceTextView)
        val taskPriorityImageView: ImageView = itemView.findViewById(R.id.taskPriorityImageView)
        val taskAvatarImageView: ImageView = itemView.findViewById(R.id.taskAvatarImageView)
        // Добавьте остальные View, если нужно
    }

    // Функция для определения ресурса изображения приоритета по его значению
    private fun getPriorityImage(priority: Int): Int {
        return when (priority) {
            0 -> R.drawable.lowest
            1 -> R.drawable.low
            2 -> R.drawable.medium
            3 -> R.drawable.high
            4 -> R.drawable.highest
            else -> R.drawable.account_circle // Если значение приоритета неизвестно, вернем дефолтное изображение
        }
    }
}
