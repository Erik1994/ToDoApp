package com.example.todoapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.todoapp.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
): ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)

    val hideCompleted = MutableStateFlow(false)

//    private val tasksFlow = searchQuery.flatMapLatest { //flatMapLastest is ekvivalent to switchMap for liveData
//        taskDao.getTasks(it)
//    }

    private val tasksFlow = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    ) { query, sortOrder, hideComplited ->
        Triple(query, sortOrder, hideComplited)

    }.flatMapLatest { (query, sortOrder, hideComplited) ->
         taskDao.getTasks(query, sortOrder, hideComplited)
    }

    val tasks = tasksFlow.asLiveData()


}

enum class SortOrder {
    BY_NAME,
    BY_DATE
}