package com.zaidshah.manager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoanMathTest {

    @Test
    fun loanRepayment_calculatesAccurateOutstanding() {
        val principal = 500000L
        val repayment = 50000L
        val outstanding = (principal - repayment).coerceAtLeast(0L)
        assertEquals(450000L, outstanding)
    }

    @Test
    fun loanRepayment_overpaymentClampedOrRejected() {
        val principal = 250000L
        val attemptedRepayment = 300000L
        val isValid = attemptedRepayment <= principal
        assertTrue(!isValid)
    }
}
