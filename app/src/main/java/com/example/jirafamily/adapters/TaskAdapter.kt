import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.jirafamily.DTO.Task
import com.example.jirafamily.R
import com.example.jirafamily.TasksActivity
import com.example.jirafamily.adapters.CircleTransformation
import com.squareup.picasso.Picasso

class TaskAdapter(val tasks: ArrayList<Task>, private val onTaskDeleteListener: TasksActivity) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {



    interface OnTaskDeleteListener {
        fun onTaskDelete(position: Int)
    }

    interface OnTaskClickListener {
        fun onTaskClick(task: Task)
    }

    private var onTaskClickListener: OnTaskClickListener? = null

    fun setOnTaskClickListener(listener: OnTaskClickListener) {
        onTaskClickListener = listener
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
                Glide.with(holder.itemView)
                    .load(url)
                    .apply(RequestOptions()
                        .placeholder(R.drawable.account_circle)
                        .error(R.drawable.account_circle)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(40, 40) // Установка размера 40x40
                        .centerCrop() // Обрезка изображения до соотношения сторон
                        .transform(CircleCrop()) // Преобразование изображения в круг
                    )
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
            onTaskClickListener?.onTaskClick(currentTask)
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