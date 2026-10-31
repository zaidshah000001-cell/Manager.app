package com.zaidshah.manager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

enum class GoalType {
    DAILY,
    WEEKLY,
    MONTHLY
}

@Entity(tableName = "goal_targets")
data class GoalTargetEntity(
    @PrimaryKey val type: GoalType,
    val targetPaise: Long,
    val updatedAt: Instant = Instant.now()
)
