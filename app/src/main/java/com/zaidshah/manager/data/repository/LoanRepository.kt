package com.zaidshah.manager.data.repository

import com.zaidshah.manager.data.local.dao.LoanDao
import com.zaidshah.manager.data.local.dao.LoanWithRepayments
import com.zaidshah.manager.data.local.dao.PersonWithLoans
import com.zaidshah.manager.data.local.entities.LoanDirection
import com.zaidshah.manager.data.local.entities.LoanEntity
import com.zaidshah.manager.data.local.entities.PersonEntity
import com.zaidshah.manager.data.local.entities.RepaymentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant

data class LoanTotals(
    val totalBorrowedPaise: Long,
    val totalLentPaise: Long
)

class LoanRepository(private val loanDao: LoanDao) {

    fun getPeopleWithLoans(): Flow<List<PersonWithLoans>> {
        return combine(
            loanDao.getAllPeople(),
            loanDao.getAllLoans(),
            loanDao.getAllRepayments()
        ) { people, loans, repayments ->
            people.map { person ->
                val personLoans = loans.filter { it.personId == person.id }
                val loansWithReps = personLoans.map { loan ->
                    val reps = repayments.filter { it.loanId == loan.id }
                    LoanWithRepayments(loan, reps)
                }
                PersonWithLoans(person, loansWithReps)
            }
        }
    }

    fun getTotals(): Flow<LoanTotals> {
        return combine(loanDao.getAllLoans(), loanDao.getAllRepayments()) { loans, repayments ->
            var borrowed = 0L
            var lent = 0L
            loans.forEach { loan ->
                val reps = repayments.filter { it.loanId == loan.id }.sumOf { it.amountPaise }
                val balance = (loan.principalPaise - reps).coerceAtLeast(0L)
                if (loan.direction == LoanDirection.BORROWED) {
                    borrowed += balance
                } else {
                    lent += balance
                }
            }
            LoanTotals(borrowed, lent)
        }
    }

    suspend fun createPerson(name: String, phone: String = "", upiId: String = ""): Long {
        return loanDao.insertPerson(PersonEntity(name = name, phone = phone, upiId = upiId))
    }

    suspend fun createLoan(
        personId: Long,
        direction: LoanDirection,
        principalPaise: Long,
        dueAt: Instant? = null,
        note: String = ""
    ): Long {
        return loanDao.insertLoan(
            LoanEntity(
                personId = personId,
                direction = direction,
                principalPaise = principalPaise,
                issuedAt = Instant.now(),
                dueAt = dueAt,
                note = note
            )
        )
    }

    suspend fun recordRepayment(loanId: Long, amountPaise: Long, note: String = ""): Long {
        return loanDao.insertRepayment(
            RepaymentEntity(
                loanId = loanId,
                amountPaise = amountPaise,
                occurredAt = Instant.now(),
                note = note
            )
        )
    }
}
