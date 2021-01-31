package com.example.todoapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import com.example.todoapp.data.manager.PreferencesManager
import com.example.todoapp.data.manager.SortOrder
import com.example.todoapp.data.repo.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository
): ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = dataRepository.preferencesFlow()

//    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
//
//    val hideCompleted = MutableStateFlow(false)

//    private val tasksFlow = searchQuery.flatMapLatest { //flatMapLastest is ekvivalent to switchMap for liveData
//        taskDao.getTasks(it)
//    }

    private val tasksFlow = combine(
        searchQuery,
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)

    }.flatMapLatest { (query, filterPreferences) ->
        dataRepository.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        dataRepository.onSortOrderSelected(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        dataRepository.onHideCompletedClick(hideCompleted)
    }

    fun onTaskSelected(task: Task) {

    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        dataRepository.onTaskCheckedChanged(task, isChecked)
    }

}

