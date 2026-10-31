package com.zaidshah.manager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaidshah.manager.data.local.entities.GoalCompletionEntity
import com.zaidshah.manager.data.local.entities.GoalTargetEntity
import com.zaidshah.manager.data.local.entities.GoalType
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goal_targets")
    fun getAllTargets(): Flow<List<GoalTargetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setTarget(target: GoalTargetEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: GoalCompletionEntity): Long

    @Query("DELETE FROM goal_completions WHERE goalType = :goalType AND periodKey = :periodKey")
    suspend fun revokeCompletion(goalType: GoalType, periodKey: String)

    @Query("SELECT * FROM goal_completions ORDER BY completedAt DESC LIMIT :limit")
    fun getRecentCompletions(limit: Int): Flow<List<GoalCompletionEntity>>
}
