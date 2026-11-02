
package com.spiritual.magicnumbers.test

import com.spiritual.magicnumbers.NumerologyEngine
import com.spiritual.magicnumbers.R
import org.junit.Assert.*
import org.junit.Test

class GetResonanceTest {

    private val engine = NumerologyEngine

    // =====================================================
    // LOGGING HELPER
    // =====================================================
    private fun log(input: String, expectedTitle: Int?, expectedMeaning: Int?, expectedFocus: Int?, result: Triple<Int, Int, Int>?) {
        println("")
        println("=====================================================")
        println("GET RESONANCE TEST")
        println("Input:     '$input'")

        if (result == null) {
            println("Result:    NULL")
            println("Expected:  NULL")
            println("Status:    ${if (expectedTitle == null) "PASS" else "FAIL"}")
            println("=====================================================")
            return
        }

        println("Title ID:   ${result.first}")
        println("Meaning ID: ${result.second}")
        println("Focus ID:   ${result.third}")
        println("Expected title:   $expectedTitle")
        println("Expected meaning: $expectedMeaning")
        println("Expected focus:   $expectedFocus")
        val passed = result.first == expectedTitle && result.second == expectedMeaning && result.third == expectedFocus
        println("Status: ${if (passed) "PASS" else "FAIL"}")
        println("=====================================================")
    }

    // =====================================================
    // ASSERT HELPER
    // =====================================================
    private fun assertResonance(input: String, title: Int, meaning: Int, focus: Int) {
        val result = engine.getResonance(input)
        log(input, title, meaning, focus, result)
        assertNotNull("Expected resonance for input: $input", result)
        assertEquals("Wrong title for $input", title, result!!.first)
        assertEquals("Wrong meaning for $input", meaning, result.second)
        assertEquals("Wrong focus for $input", focus, result.third)
    }

    private fun assertInvalid(input: String) {
        val result = engine.getResonance(input)
        log(input, null, null, null, result)
        assertNull("Expected NULL for invalid input: '$input'", result)
    }

    // =====================================================
    // 4-DIGIT PATTERNS
    // =====================================================
    @Test
    fun testResonanceMono4() {
        listOf(
            "0000", "1111", "2222", "3333", "4444",
            "5555", "6666", "7777", "8888", "9999"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance4_title_mono,
                R.string.resonance4_meaning_mono,
                R.string.resonance_focus_none
            )
        }
    }

    @Test
    fun testResonanceAABB4() {
        listOf(
            "0011", "1122", "2233", "3344", "4455",
            "5566", "6677", "7788", "8899", "9900",
            "1199", "2255", "7733"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance4_title_aabb,
                R.string.resonance4_meaning_aabb,
                R.string.resonance_focus_frame
            )
        }
    }

    @Test
    fun testResonanceABAB4() {
        listOf(
            "0101", "1212", "2323", "3434", "4545",
            "5656", "6767", "7878", "8989", "9090",
            "1919", "2727", "3838"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance4_title_abab,
                R.string.resonance4_meaning_abab,
                R.string.resonance_focus_center
            )
        }
    }

    @Test
    fun testResonanceABBA4() {
        listOf(
            "0110", "1221", "2332", "3443", "4554",
            "5665", "6776", "7887", "8998", "9009",
            "2112", "3773", "4884"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance4_title_abba,
                R.string.resonance4_meaning_abba,
                R.string.resonance_focus_center
            )
        }
    }

    @Test
    fun testResonanceABCD4() {
        listOf(
            "0123", "1357", "2468", "9876", "1029",
            "5831", "7942", "3142", "5678", "9081"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance4_title_abcd,
                R.string.resonance4_meaning_abcd,
                R.string.resonance_focus_none
            )
        }
    }

    @Test
    fun testResonanceNone4() {
        listOf(
            "1123", "1112", "1223", "1213", "1122".replace("1122", "1121"),
            "1233", "1222", "1113", "2234", "4456",
            "1234".replace("1234", "1124")
        ).forEach {
            assertResonance(
                it,
                R.string.resonance4_title_none,
                R.string.resonance4_meaning_none,
                R.string.resonance_focus_none
            )
        }
    }

    // =====================================================
    // 6-DIGIT PATTERNS
    // =====================================================
    @Test
    fun testResonanceMono6() {
        listOf(
            "000000", "111111", "222222", "333333",
            "444444", "555555", "666666", "777777",
            "888888", "999999"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance6_title_mono,
                R.string.resonance6_meaning_mono,
                R.string.resonance_focus_none
            )
        }
    }

    @Test
    fun testResonanceAAABBB6() {
        listOf(
            "000111", "111222", "222333", "333444",
            "444555", "555666", "666777", "777888",
            "888999", "999000", "111999", "222888"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance6_title_aaabbb,
                R.string.resonance6_meaning_aaabbb,
                R.string.resonance_focus_frame
            )
        }
    }

    @Test
    fun testResonanceAABBCC6() {
        listOf(
            "001122", "112233", "223344", "334455",
            "445566", "556677", "667788", "778899",
            "889900", "990011", "113355", "224466"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance6_title_aabbcc,
                R.string.resonance6_meaning_aabbcc,
                R.string.resonance_focus_none
            )
        }
    }

    @Test
    fun testResonanceABABAB6() {
        listOf(
            "010101", "121212", "232323", "343434",
            "454545", "565656", "676767", "787878",
            "898989", "909090", "191919", "272727"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance6_title_ababab,
                R.string.resonance6_meaning_ababab,
                R.string.resonance_focus_center
            )
        }
    }

    @Test
    fun testResonanceABCABC6() {
        listOf(
            "123123", "456456", "789789", "012012",
            "135135", "246246", "357357", "468468",
            "579579", "908908"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance6_title_abcabc,
                R.string.resonance6_meaning_abcabc,
                R.string.resonance_focus_center
            )
        }
    }

    @Test
    fun testResonanceMirror6() {
        listOf(
            "100001", "123321", "456654", "789987",
            "012210", "135531", "246642", "987789",
            "500005", "900009"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance6_title_mirror,
                R.string.resonance6_meaning_mirror,
                R.string.resonance_focus_center
            )
        }
    }

    @Test
    fun testResonanceNone6() {
        listOf(
            "123456", "123457", "112345", "123455",
            "123443", "123454", "112345", "123789",
            "102938", "583729", "987654", "123654"
        ).forEach {
            assertResonance(
                it,
                R.string.resonance6_title_none,
                R.string.resonance6_meaning_none,
                R.string.resonance_focus_none
            )
        }
    }

    // =====================================================
    // INVALID LENGTH
    // =====================================================
    @Test
    fun testResonanceInvalidLength() {
        listOf(
            "", "1", "12", "123",
            "12345", "1234567", "12345678",
            "123456789", "1234567890"
        ).forEach {
            assertInvalid(it)
        }
    }

    // =====================================================
    // INVALID CHARACTERS
    // =====================================================
    @Test
    fun testResonanceInvalidCharacters() {
        listOf(
            "12A4", "12 4", "12-4", "12.4",
            "ABCD", "ABCDEF", "12#4", "12@4",
            "12/4", "12_4", "12+4", "12,4",
            "12\n4", "12\t4"
        ).forEach {
            assertInvalid(it)
        }
    }
}