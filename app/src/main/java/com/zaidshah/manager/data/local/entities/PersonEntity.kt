package com.zaidshah.manager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "people")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val upiId: String = "",
    val avatarId: Int = 0
)
