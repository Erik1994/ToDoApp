package com.example.todoapp.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.Task
import com.example.todoapp.databinding.ItemTasksBinding

class TaskAdapter : ListAdapter<Task, TaskAdapter.TasksViewHolder>(DiffCallback()) {
    private var onItemClick: ((Task) -> Unit)? = null
    private var onCheckBoxClick: ((Task, Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val binding = ItemTasksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    fun setOnItemClickListener(onItemClick: (Task) -> Unit) {
        this.onItemClick = onItemClick
    }

    fun setOnCheckBoxClickListener(onCheckBoxClick: (Task, Boolean) -> Unit) {
        this.onCheckBoxClick = onCheckBoxClick
    }

    inner class TasksViewHolder(private val binding: ItemTasksBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        onItemClick?.invoke(task)
                    }
                }

                checkBoxCompleted.setOnClickListener{
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        onCheckBoxClick?.invoke(task, checkBoxCompleted.isChecked)
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}