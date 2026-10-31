package com.zaidshah.manager.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

enum class LoanDirection {
    BORROWED,
    LENT
}

@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["personId"])]
)
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personId: Long,
    val direction: LoanDirection,
    val principalPaise: Long,
    val issuedAt: Instant,
    val dueAt: Instant? = null,
    val note: String = ""
)
