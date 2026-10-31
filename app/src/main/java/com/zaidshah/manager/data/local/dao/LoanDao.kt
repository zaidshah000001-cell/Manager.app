package com.zaidshah.manager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaidshah.manager.data.local.entities.LoanDirection
import com.zaidshah.manager.data.local.entities.LoanEntity
import com.zaidshah.manager.data.local.entities.PersonEntity
import com.zaidshah.manager.data.local.entities.RepaymentEntity
import kotlinx.coroutines.flow.Flow

data class PersonWithLoans(
    val person: PersonEntity,
    val loans: List<LoanWithRepayments>
)

data class LoanWithRepayments(
    val loan: LoanEntity,
    val repayments: List<RepaymentEntity>
) {
    val totalRepaidPaise: Long get() = repayments.sumOf { it.amountPaise }
    val outstandingPaise: Long get() = (loan.principalPaise - totalRepaidPaise).coerceAtLeast(0L)
    val isFullyRepaid: Boolean get() = outstandingPaise == 0L
}

@Dao
interface LoanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity): Long

    @Query("SELECT * FROM people ORDER BY name ASC")
    fun getAllPeople(): Flow<List<PersonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long

    @Query("SELECT * FROM loans WHERE personId = :personId")
    fun getLoansForPerson(personId: Long): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepayment(repayment: RepaymentEntity): Long

    @Query("SELECT * FROM repayments WHERE loanId = :loanId")
    fun getRepaymentsForLoan(loanId: Long): Flow<List<RepaymentEntity>>

    @Query("SELECT * FROM repayments")
    fun getAllRepayments(): Flow<List<RepaymentEntity>>
}
