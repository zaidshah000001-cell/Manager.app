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

    private val now = LocalDate.now()
    private val week = DateUtils.getWeekPeriod(now)
    private val month = DateUtils.getMonthPeriod(now)
    private val dayEarned = repo.getEarnedForPeriod(
        DateUtils.toStartOfDayInstant(now), DateUtils.toEndOfDayInstant(now)
    )
    private val weekEarned = repo.getEarnedForPeriod(week.first, week.second)
    private val monthEarned = repo.getEarnedForPeriod(month.first, month.second)

    private fun progress(type: GoalType, target: Long, earned: Long): GoalProgressUi {
        val pct = if (target > 0L) {
            (earned.toDouble() / target.toDouble() * 100).toInt().coerceIn(0, 100)
        } else 0
        return GoalProgressUi(type, target, earned, pct)
    }

    val uiState: StateFlow<GoalsUiState> = combine(
        repo.getGoalTargets(),
        dayEarned,
        weekEarned,
        monthEarned,
        repo.getRecentCompletions(100)
    ) { targets, daily, weekly, monthly, completions ->
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
        // Completion keys are unique in Room; repeated emissions do not duplicate history.
        val weekKey = week.first.toString().take(10)
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
