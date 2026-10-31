package com.zaidshah.manager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaidshah.manager.data.local.entities.TransactionEntity
import com.zaidshah.manager.data.local.entities.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Query("SELECT * FROM transactions WHERE occurredAt BETWEEN :start AND :end ORDER BY occurredAt DESC")
    fun getTransactionsBetween(start: Instant, end: Instant): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY occurredAt DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amountPaise) FROM transactions WHERE type = :type AND occurredAt BETWEEN :start AND :end")
    fun getTotalAmountByTypeBetween(type: TransactionType, start: Instant, end: Instant): Flow<Long?>
}
