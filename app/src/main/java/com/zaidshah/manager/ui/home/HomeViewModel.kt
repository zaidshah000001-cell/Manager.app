package com.zaidshah.manager.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zaidshah.manager.data.local.AppDatabase
import com.zaidshah.manager.data.local.entities.GoalType
import com.zaidshah.manager.data.local.entities.TransactionEntity
import com.zaidshah.manager.data.local.entities.TransactionType
import com.zaidshah.manager.data.preferences.AppPreferences
import com.zaidshah.manager.data.repository.FinanceRepository
import com.zaidshah.manager.data.repository.LoanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class LoanTileUi(
    val name: String,
    val initial: String,
    val direction: String,
    val balancePaise: Long
)

data class HomeUiState(
    val profileName: String = "Zaid",
    val avatarId: Int = 0,
    val selectedDate: LocalDate = LocalDate.now(),
    val earnedPaise: Long = 0L,
    val spentPaise: Long = 0L,
    val netPaise: Long = 0L,
    val dailyGoalPaise: Long = 0L,
    val goalPercentage: Int = 0,
    val recentLoans: List<LoanTileUi> = emptyList(),
    val recentTransactions: List<TransactionEntity> = emptyList()
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val financeRepo = FinanceRepository(db.transactionDao(), db.goalDao())
    private val loanRepo = LoanRepository(db.loanDao())
    private val prefs = AppPreferences(application)

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val dailySummary = _selectedDate.flatMapLatest { date ->
        financeRepo.getDailySummary(date)
    }

    private val goalTarget = financeRepo.getGoalTargets().flatMapLatest { targets ->
        val daily = targets.find { it.type == GoalType.DAILY }?.targetPaise ?: 0L
        MutableStateFlow(daily)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        prefs.profileName,
        prefs.avatarId,
        _selectedDate,
        dailySummary,
        goalTarget,
        loanRepo.getPeopleWithLoans(),
        financeRepo.getRecentTransactions(5)
    ) { name, avatarId, date, summary, target, peopleWithLoans, txs ->
        val pct = if (target > 0L) {
            ((summary.earnedPaise.toDouble() / target.toDouble()) * 100).toInt().coerceAtMost(100)
        } else {
            0
        }

        val tiles = peopleWithLoans.take(3).mapNotNull { p ->
            val firstLoan = p.loans.firstOrNull() ?: return@mapNotNull null
            LoanTileUi(
                name = p.person.name,
                initial = p.person.name.firstOrNull()?.uppercase() ?: "P",
                direction = if (firstLoan.loan.direction == com.zaidshah.manager.data.local.entities.LoanDirection.BORROWED) "Borrowed" else "Lent",
                balancePaise = firstLoan.outstandingPaise
            )
        }

        HomeUiState(
            profileName = name,
            avatarId = avatarId,
            selectedDate = date,
            earnedPaise = summary.earnedPaise,
            spentPaise = summary.spentPaise,
            netPaise = summary.netPaise,
            dailyGoalPaise = target,
            goalPercentage = pct,
            recentLoans = tiles,
            recentTransactions = txs
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addTransaction(type: TransactionType, amountPaise: Long, note: String, category: String) {
        viewModelScope.launch {
            financeRepo.addTransaction(type, amountPaise, note, category, _selectedDate.value)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            financeRepo.deleteTransaction(id)
        }
    }
}
