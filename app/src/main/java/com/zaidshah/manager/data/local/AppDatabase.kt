package com.zaidshah.manager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zaidshah.manager.data.local.dao.GoalDao
import com.zaidshah.manager.data.local.dao.LoanDao
import com.zaidshah.manager.data.local.dao.TransactionDao
import com.zaidshah.manager.data.local.entities.GoalCompletionEntity
import com.zaidshah.manager.data.local.entities.GoalTargetEntity
import com.zaidshah.manager.data.local.entities.LoanEntity
import com.zaidshah.manager.data.local.entities.PersonEntity
import com.zaidshah.manager.data.local.entities.RepaymentEntity
import com.zaidshah.manager.data.local.entities.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        GoalTargetEntity::class,
        GoalCompletionEntity::class,
        PersonEntity::class,
        LoanEntity::class,
        RepaymentEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao
    abstract fun loanDao(): LoanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "manager_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
