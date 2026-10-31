package com.zaidshah.manager.ui.goals

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zaidshah.manager.data.local.AppDatabase
import com.zaidshah.manager.data.local.entities.GoalCompletionEntity
import com.zaidshah.manager.data.local.entities.GoalType
import com.zaidshah.manager.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.delay
import com.zaidshah.manager.util.DateUtils
import java.time.temporal.WeekFields
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class GoalProgressUi(
    val type: GoalType,
    val targetPaise: Long,
    val earnedPaise: Long,
    val percentage: Int
)

data class GoalsUiState(
    val dailyProgress: GoalProgressUi = GoalProgressUi(GoalType.DAILY, 0, 0, 0),
    val weeklyProgress: GoalProgressUi = GoalProgressUi(GoalType.WEEKLY, 0, 0, 0),
    val monthlyProgress: GoalProgressUi = GoalProgressUi(GoalType.MONTHLY, 0, 0, 0),
    val completedGoals: List<GoalCompletionEntity> = emptyList()
)

class GoalsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val repo = FinanceRepository(db.transactionDao(), db.goalDao())

    private val today = MutableStateFlow(LocalDate.now())
    init {
        viewModelScope.launch {
            while (true) {
                val next = LocalDate.now().plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()
                delay(java.time.Duration.between(java.time.Instant.now(), next).toMillis().coerceAtLeast(100L) + 100L)
                today.value = LocalDate.now()
            }
        }
    }
    private val now get() = today.value
    private val dayEarned = today.flatMapLatest { d -> repo.getEarnedForPeriod(DateUtils.toStartOfDayInstant(d), DateUtils.toEndOfDayInstant(d)) }
    private val weekEarned = today.flatMapLatest { d -> val w = DateUtils.getWeekPeriod(d); repo.getEarnedForPeriod(w.first, w.second) }
    private val monthEarned = today.flatMapLatest { d -> val m = DateUtils.getMonthPeriod(d); repo.getEarnedForPeriod(m.first, m.second) }

    private fun progress(type: GoalType, target: Long, earned: Long): GoalProgressUi {
        val pct = if (target > 0L) {
            (earned.toDouble() / target.toDouble() * 100).toInt().coerceIn(0, 100)
        } else 0
        return GoalProgressUi(type, target, earned, pct)
    }

    private var lastSyncedProgress: List<Any>? = null

    val uiState: StateFlow<GoalsUiState> = combine(
        repo.getGoalTargets(),
        today,
        dayEarned,
        weekEarned,
        monthEarned,
        repo.getRecentCompletions(100)
    ) { values ->
        @Suppress("UNCHECKED_CAST") val targets = values[0] as List<com.zaidshah.manager.data.local.entities.GoalTargetEntity>
        val daily = values[2] as Long
        val weekly = values[3] as Long
        val monthly = values[4] as Long
        @Suppress("UNCHECKED_CAST") val completions = values[5] as List<GoalCompletionEntity>
        val dailyTarget = targets.find { it.type == GoalType.DAILY }?.targetPaise ?: 100000L
        val weeklyTarget = targets.find { it.type == GoalType.WEEKLY }?.targetPaise ?: 500000L
        val monthlyTarget = targets.find { it.type == GoalType.MONTHLY }?.targetPaise ?: 2000000L
        GoalsUiState(
            dailyProgress = progress(GoalType.DAILY, dailyTarget, daily),
            weeklyProgress = progress(GoalType.WEEKLY, weeklyTarget, weekly),
            monthlyProgress = progress(GoalType.MONTHLY, monthlyTarget, monthly),
            completedGoals = completions
        )
    }.onEach { state ->
        val syncKey = listOf(
            now.toString(),
            state.dailyProgress,
            state.weeklyProgress,
            state.monthlyProgress
        )
        if (syncKey == lastSyncedProgress) return@onEach
        lastSyncedProgress = syncKey

        // Completion keys are unique in Room; repeated emissions do not duplicate history.
        val weekKey = now.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).toString()
        repo.syncCurrentCompletion(GoalType.DAILY, now.toString(),
            state.dailyProgress.earnedPaise, state.dailyProgress.targetPaise)
        repo.syncCurrentCompletion(GoalType.WEEKLY, weekKey,
            state.weeklyProgress.earnedPaise, state.weeklyProgress.targetPaise)
        repo.syncCurrentCompletion(GoalType.MONTHLY, now.toString().take(7),
            state.monthlyProgress.earnedPaise, state.monthlyProgress.targetPaise)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GoalsUiState())

    fun updateTarget(type: GoalType, targetPaise: Long) {
        viewModelScope.launch {
            repo.setGoalTarget(type, targetPaise)
        }
    }
}
