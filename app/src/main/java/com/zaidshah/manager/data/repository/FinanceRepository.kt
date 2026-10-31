package com.zaidshah.manager.data.repository

import com.zaidshah.manager.data.local.dao.GoalDao
import com.zaidshah.manager.data.local.dao.TransactionDao
import com.zaidshah.manager.data.local.entities.GoalCompletionEntity
import com.zaidshah.manager.data.local.entities.GoalTargetEntity
import com.zaidshah.manager.data.local.entities.GoalType
import com.zaidshah.manager.data.local.entities.TransactionEntity
import com.zaidshah.manager.data.local.entities.TransactionType
import com.zaidshah.manager.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate

data class DailySummary(
    val earnedPaise: Long,
    val spentPaise: Long,
    val netPaise: Long
)

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao
) {
    fun getTransactionsForDate(date: LocalDate): Flow<List<TransactionEntity>> {
        val start = DateUtils.toStartOfDayInstant(date)
        val end = DateUtils.toEndOfDayInstant(date)
        return transactionDao.getTransactionsBetween(start, end)
    }

    fun getDailySummary(date: LocalDate): Flow<DailySummary> {
        val start = DateUtils.toStartOfDayInstant(date)
        val end = DateUtils.toEndOfDayInstant(date)
        val earnedFlow = transactionDao.getTotalAmountByTypeBetween(TransactionType.EARN, start, end)
        val spentFlow = transactionDao.getTotalAmountByTypeBetween(TransactionType.SPEND, start, end)

        return combine(earnedFlow, spentFlow) { earned, spent ->
            val e = earned ?: 0L
            val s = spent ?: 0L
            DailySummary(earnedPaise = e, spentPaise = s, netPaise = e - s)
        }
    }

    fun getEarnedForPeriod(start: Instant, end: Instant): Flow<Long> =
        transactionDao.getTotalAmountByTypeBetween(TransactionType.EARN, start, end)
            .map { it ?: 0L }

    suspend fun syncCurrentCompletion(type: GoalType, periodKey: String, earned: Long, target: Long) {
        if (target <= 0L) return
        if (earned >= target) {
            goalDao.insertCompletion(
                GoalCompletionEntity(goalType = type, periodKey = periodKey, targetPaise = target)
            )
        } else {
            goalDao.revokeCompletion(type, periodKey)
        }
    }

    fun getRecentTransactions(limit: Int = 5): Flow<List<TransactionEntity>> =
        transactionDao.getRecentTransactions(limit)

    suspend fun addTransaction(
        type: TransactionType,
        amountPaise: Long,
        note: String,
        category: String,
        date: LocalDate
    ) {
        val instant = if (date == LocalDate.now()) Instant.now() else DateUtils.toStartOfDayInstant(date)
        val entity = TransactionEntity(
            type = type,
            amountPaise = amountPaise,
            note = note,
            category = category,
            occurredAt = instant
        )
        transactionDao.insertTransaction(entity)
    }

    suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransaction(id)
    }

    fun getGoalTargets(): Flow<List<GoalTargetEntity>> = goalDao.getAllTargets()

    suspend fun setGoalTarget(type: GoalType, targetPaise: Long) {
        goalDao.setTarget(GoalTargetEntity(type = type, targetPaise = targetPaise))
    }

    fun getRecentCompletions(limit: Int = 24): Flow<List<GoalCompletionEntity>> =
        goalDao.getRecentCompletions(limit)
}
