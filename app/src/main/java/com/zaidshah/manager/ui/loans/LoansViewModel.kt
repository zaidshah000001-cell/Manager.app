package com.zaidshah.manager.ui.loans

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zaidshah.manager.data.local.AppDatabase
import com.zaidshah.manager.data.local.dao.PersonWithLoans
import com.zaidshah.manager.data.local.entities.LoanDirection
import com.zaidshah.manager.data.repository.LoanRepository
import com.zaidshah.manager.data.repository.LoanTotals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LoanFilter {
    ALL,
    BORROWED,
    LENT
}

data class LoansUiState(
    val borrowedPaise: Long = 0L,
    val lentPaise: Long = 0L,
    val filter: LoanFilter = LoanFilter.ALL,
    val people: List<PersonWithLoans> = emptyList()
)

class LoansViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val repo = LoanRepository(db.loanDao())

    private val _filter = MutableStateFlow(LoanFilter.ALL)
    val filter: StateFlow<LoanFilter> = _filter.asStateFlow()

    val uiState: StateFlow<LoansUiState> = combine(
        repo.getTotals(),
        repo.getPeopleWithLoans(),
        _filter
    ) { totals: LoanTotals, people: List<PersonWithLoans>, currentFilter: LoanFilter ->
        val filtered = when (currentFilter) {
            LoanFilter.ALL -> people
            LoanFilter.BORROWED -> people.filter { p ->
                p.loans.any { it.loan.direction == LoanDirection.BORROWED && !it.isFullyRepaid }
            }
            LoanFilter.LENT -> people.filter { p ->
                p.loans.any { it.loan.direction == LoanDirection.LENT && !it.isFullyRepaid }
            }
        }

        LoansUiState(
            borrowedPaise = totals.totalBorrowedPaise,
            lentPaise = totals.totalLentPaise,
            filter = currentFilter,
            people = filtered
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LoansUiState())

    fun setFilter(filter: LoanFilter) {
        _filter.value = filter
    }

    fun addLoan(name: String, phone: String, upiId: String, direction: LoanDirection, principalPaise: Long) {
        viewModelScope.launch {
            val personId = repo.createPerson(name, phone, upiId)
            repo.createLoan(personId, direction, principalPaise)
        }
    }

    fun recordRepayment(loanId: Long, amountPaise: Long) {
        viewModelScope.launch {
            repo.recordRepayment(loanId, amountPaise)
        }
    }
}
