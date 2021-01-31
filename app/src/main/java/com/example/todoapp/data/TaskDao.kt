package com.example.todoapp.data

import androidx.room.*
import com.example.todoapp.data.manager.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTasks(query: String, sortOrder: SortOrder, hidecompleted: Boolean): Flow<List<Task>> =
        when(sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedByDate(query, hidecompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hidecompleted)
        }

    //|| -> append operator in SQL Lite
    @Query("SELECT * FROM TASK_TABLE WHERE (completed != :hidecompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, name")
    fun getTasksSortedByName(searchQuery: String, hidecompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM TASK_TABLE WHERE (completed != :hidecompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getTasksSortedByDate(searchQuery: String, hidecompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

}