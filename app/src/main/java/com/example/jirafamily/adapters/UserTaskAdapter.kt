import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Task
import com.example.jirafamily.R
import com.example.jirafamily.adapters.CircleTransformation
import com.squareup.picasso.Picasso

class UserTaskAdapter(val tasks: ArrayList<Task>, private val onTaskClickListener: OnTaskClickListener) : RecyclerView.Adapter<UserTaskAdapter.TaskViewHolder>() {

    interface OnTaskClickListener {
        fun onTaskClick(task: Task)
    }

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
        currentTask.avatarUrl?.let { url ->
            if (url.isNotEmpty()) {
                Picasso.get().load(url)
                    .resize(40, 40) // Установка размера 40x40
                    .centerCrop() // Обрезка изображения до соотношения сторон
                    .placeholder(R.drawable.account_circle) // Дефолтное изображение
                    .transform(CircleTransformation())
                    .into(holder.taskAvatarImageView)
            } else {
                // Загрузка дефолтного изображения, если ссылка пуста
                holder.taskAvatarImageView.setImageResource(R.drawable.account_circle)
            }
        } ?: run {
            // Если ссылка null, загрузка дефолтного изображения
            holder.taskAvatarImageView.setImageResource(R.drawable.account_circle)
        }

        // Обработка клика на элемент списка для удаления
        holder.itemView.setOnClickListener {
            onTaskClickListener.onTaskClick(currentTask)
        }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
    fun removeTask(task: Task) {
        val index = tasks.indexOf(task)
        if (index != -1) {
            tasks.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        val importanceTextView: TextView = itemView.findViewById(R.id.importanceTextView)
        val taskPriorityImageView: ImageView = itemView.findViewById(R.id.taskPriorityImageView)
        val taskAvatarImageView: ImageView = itemView.findViewById(R.id.taskAvatarImageView)
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
