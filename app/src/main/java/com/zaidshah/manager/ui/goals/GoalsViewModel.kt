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
    private val todayTransactions = repo.getTransactionsForDate(now)

    val uiState: StateFlow<GoalsUiState> = combine(
        repo.getGoalTargets(),
        todayTransactions,
        repo.getRecentCompletions(24)
    ) { targets, _, completions ->
        val dailyTarget = targets.find { it.type == GoalType.DAILY }?.targetPaise ?: 100000L
        val weeklyTarget = targets.find { it.type == GoalType.WEEKLY }?.targetPaise ?: 500000L
        val monthlyTarget = targets.find { it.type == GoalType.MONTHLY }?.targetPaise ?: 2000000L

        GoalsUiState(
            dailyProgress = GoalProgressUi(GoalType.DAILY, dailyTarget, 0L, 0),
            weeklyProgress = GoalProgressUi(GoalType.WEEKLY, weeklyTarget, 0L, 0),
            monthlyProgress = GoalProgressUi(GoalType.MONTHLY, monthlyTarget, 0L, 0),
            completedGoals = completions
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GoalsUiState())

    fun updateTarget(type: GoalType, targetPaise: Long) {
        viewModelScope.launch {
            repo.setGoalTarget(type, targetPaise)
        }
    }
}
