package com.example.todoapp.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.todoapp.data.Task
import com.example.todoapp.data.manager.SortOrder
import com.example.todoapp.data.repo.DataRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    private val taskEventChannel = Channel<TaskEvent>()
    val tasksEvent: Flow<TaskEvent>
        get() = taskEventChannel.receiveAsFlow()

    val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFlow = dataRepository.preferencesFlow()

//    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
//
//    val hideCompleted = MutableStateFlow(false)

//    private val tasksFlow = searchQuery.flatMapLatest { //flatMapLastest is ekvivalent to switchMap for liveData
//        taskDao.getTasks(it)
//    }

    private val tasksFlow = combine(
        searchQuery.asFlow(),
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

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        dataRepository.onTaskCheckedChanged(task, isChecked)
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        dataRepository.onUndoDeleteClick(task)
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        dataRepository.onTaskSwiped(task)
        taskEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    sealed class TaskEvent {
        object NavigateToAddTaskScreen : TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
    }

}

