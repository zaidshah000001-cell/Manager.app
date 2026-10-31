package com.zaidshah.manager.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

enum class TransactionType {
    EARN,
    SPEND
}

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["occurredAt"]), Index(value = ["type"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val amountPaise: Long,
    val note: String,
    val category: String,
    val occurredAt: Instant,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
