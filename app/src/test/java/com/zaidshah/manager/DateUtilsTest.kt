package com.zaidshah.manager

import com.zaidshah.manager.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateUtilsTest {

    @Test
    fun weekPeriod_startsOnMondayEndsOnSunday() {
        val wednesday = LocalDate.of(2026, 10, 28)
        val (start, end) = DateUtils.getWeekPeriod(wednesday)
        val startLd = start.atZone(DateUtils.Zone).toLocalDate()
        val endLd = end.atZone(DateUtils.Zone).toLocalDate()

        assertEquals(java.time.DayOfWeek.MONDAY, startLd.dayOfWeek)
        assertEquals(java.time.DayOfWeek.SUNDAY, endLd.dayOfWeek)
        assertEquals(LocalDate.of(2026, 10, 26), startLd)
        assertEquals(LocalDate.of(2026, 11, 1), endLd)
    }
}
