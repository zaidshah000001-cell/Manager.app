package com.zaidshah.manager.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "goal_completions",
    indices = [Index(value = ["goalType", "periodKey"], unique = true)]
)
data class GoalCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalType: GoalType,
    val periodKey: String,
    val completedAt: Instant = Instant.now(),
    val targetPaise: Long,
    val status: String = "COMPLETED"
)
