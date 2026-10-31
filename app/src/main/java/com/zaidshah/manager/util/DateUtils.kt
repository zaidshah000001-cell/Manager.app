package com.zaidshah.manager.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

object DateUtils {
    val Zone: ZoneId = ZoneId.systemDefault()

    fun getGreeting(now: LocalTime = LocalTime.now()): String {
        return when (now.hour) {
            in 4..11 -> "Good morning!"
            in 12..16 -> "Good afternoon!"
            else -> "Good evening!"
        }
    }

    fun toStartOfDayInstant(date: LocalDate): Instant =
        date.atStartOfDay(Zone).toInstant()

    fun toEndOfDayInstant(date: LocalDate): Instant =
        date.atTime(LocalTime.MAX).atZone(Zone).toInstant()

    fun getWeekPeriod(date: LocalDate): Pair<Instant, Instant> {
        val monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        return Pair(toStartOfDayInstant(monday), toEndOfDayInstant(sunday))
    }

    fun getMonthPeriod(date: LocalDate): Pair<Instant, Instant> {
        val firstDay = date.with(TemporalAdjusters.firstDayOfMonth())
        val lastDay = date.with(TemporalAdjusters.lastDayOfMonth())
        return Pair(toStartOfDayInstant(firstDay), toEndOfDayInstant(lastDay))
    }

    fun formatDisplayDate(instant: Instant): String {
        val ldt = instant.atZone(Zone).toLocalDateTime()
        return ldt.format(DateTimeFormatter.ofPattern("d MMM, h:mm a"))
    }
}
