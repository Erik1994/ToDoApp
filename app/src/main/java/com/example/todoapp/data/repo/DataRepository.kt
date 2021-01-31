package com.example.todoapp.data.repo

import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import com.example.todoapp.data.manager.PreferencesManager
import com.example.todoapp.data.manager.SortOrder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val prefrencesManager: PreferencesManager
) {
    fun preferencesFlow() = prefrencesManager.preferences


    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean) =
        taskDao.getTasks(query, sortOrder, hideCompleted)

    suspend fun onSortOrderSelected(sortOrder: SortOrder) =
        prefrencesManager.updateSortOrder(sortOrder)

    suspend fun onHideCompletedClick(hideCompleted: Boolean) =
        prefrencesManager.updateHideCompleted(hideCompleted)

    fun onTaskSelected(task: Task) {

    }

    suspend fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = taskDao.update(task.copy(completed = isChecked))
}