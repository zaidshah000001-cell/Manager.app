package com.zaidshah.manager

import com.zaidshah.manager.util.CurrencyUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CurrencyUtilsTest {

    @Test
    fun parseRupeesToPaise_handlesValidInputs() {
        assertEquals(85000L, CurrencyUtils.parseRupeesToPaise("850"))
        assertEquals(18000L, CurrencyUtils.parseRupeesToPaise("180.00"))
        assertEquals(125050L, CurrencyUtils.parseRupeesToPaise("1250.50"))
        assertEquals(50L, CurrencyUtils.parseRupeesToPaise("0.50"))
        assertEquals(500000L, CurrencyUtils.parseRupeesToPaise("₹5,000"))
    }

    @Test
    fun parseRupeesToPaise_rejectsInvalidFormats() {
        assertNull(CurrencyUtils.parseRupeesToPaise("-100"))
        assertNull(CurrencyUtils.parseRupeesToPaise("10.999"))
        assertNull(CurrencyUtils.parseRupeesToPaise("abc"))
        assertNull(CurrencyUtils.parseRupeesToPaise(""))
    }

    @Test
    fun formatPaise_formatsCorrectly() {
        val formatted = CurrencyUtils.formatPaise(85000L)
        assert(formatted.contains("850"))
    }
}
