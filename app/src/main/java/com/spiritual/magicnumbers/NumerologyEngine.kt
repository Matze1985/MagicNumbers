package com.spiritual.magicnumbers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.ui.graphics.Color
import com.spiritual.magicnumbers.Utils.cleanMarkdown
import kotlin.random.Random

data class CrossSumResult(
    val sumBeforeReduce: Int,
    val reducedSum: Int,
    val fullCalculationText: String
)

data class EngineResult(
    val number: String,
    val crossSum: CrossSumResult,
    val numerologyTitleResId: Int,
    val digitKeywords: List<Pair<Char, Int>>,
    val digitIntroResIds: List<Pair<Char, Int>>,
    val angelResIds: List<Int>,
    val angelImpulseResId: Int,
    val masterCore: Int?,
    val masterAmplifiers: List<Int>,
    val dominantRepeat: Int?,
    val frequencyScore: Float,
    val energyFlowResId: Int,
    val summaryResId: Int,
    val resonanceTitleResId: Int?,
    val resonanceMeaningResId: Int?,
    val resonanceFocusResId: Int?,
    val karmicDetailResId: Int?,
    val stateResId: Int,
    val frequencyHz: String,
    val currentTime: String?,
    val timeEnergyMessage: String?,
    val batteryLevel: Int,
    val batteryTitle: String,
    val batteryMessage: String,
    val chakraTitle: String,
    val chakraText: String,
    val personalChakraBalance: List<FibonacciChakraBalance>,
    val harmonyTitle: String,
    val harmonyText: String
)

data class FibonacciChakraBalance(
    val chakraValue: Int,
    val nameResId: Int,
    val fibonacciWeight: Int,
    val balancePercent: Int,
    val status: BalanceStatus
)

enum class BalanceStatus {
    BALANCED,
    ADJUSTMENT,
    IMBALANCED
}

object NumerologyEngine {

    private val masterCoreNumbers = listOf(11, 22, 33, 44)
    private val masterAmplifierNumbers = listOf(11, 22, 33, 44, 55, 66, 77, 88, 99)
    private val fibonacciChakraWeights = listOf(1, 1, 2, 3, 5, 8, 13)

    // --------------------------------------------------
    // RANDOM NUMBER GENERATOR
    // --------------------------------------------------
    fun generateNumber(length: Int = 6): String {
        val random = Random(System.currentTimeMillis())
        return (1..length).joinToString("") {
            random.nextInt(0, 10).toString()
        }
    }

    // =====================================================
    // CROSS SUM
    // =====================================================
    fun calculateCrossSum(number: String): CrossSumResult {
        val clean = number.filter { it.isDigit() }
        if (clean.isEmpty()) return CrossSumResult(0, 0, "")

        val digits = clean.map { it.digitToInt() }
        val steps = mutableListOf<String>()
        val sum = digits.sum()
        steps.add(digits.joinToString(" + ") + " = $sum")
        var reduced = sum

        while (reduced > 9 && reduced !in masterCoreNumbers) {
            val newDigits = reduced.toString().map { it.digitToInt() }
            reduced = newDigits.sum()
            steps.add(newDigits.joinToString(" + ") + " = $reduced")
        }

        return CrossSumResult(sum, reduced, steps.joinToString(" → "))
    }

    // =====================================================
    // NUMEROLOGY TITLE
    // =====================================================
    fun getNumerologyTitleResId(value: Int) = when (value) {
        1 -> R.string.numerology_1
        2 -> R.string.numerology_2
        3 -> R.string.numerology_3
        4 -> R.string.numerology_4
        5 -> R.string.numerology_5
        6 -> R.string.numerology_6
        7 -> R.string.numerology_7
        8 -> R.string.numerology_8
        9 -> R.string.numerology_9
        11 -> R.string.numerology_11
        22 -> R.string.numerology_22
        33 -> R.string.numerology_33
        44 -> R.string.numerology_44
        55 -> R.string.numerology_55
        66 -> R.string.numerology_66
        77 -> R.string.numerology_77
        88 -> R.string.numerology_88
        99 -> R.string.numerology_99
        else -> R.string.numerology_unknown
    }

    // =====================================================
    // DIGITS
    // =====================================================
    fun getDigitKeywords(number: String) =
        number.toCharArray().distinct().mapNotNull {
            val res = when (it) {
                '0' -> R.string.keyword_0
                '1' -> R.string.keyword_1
                '2' -> R.string.keyword_2
                '3' -> R.string.keyword_3
                '4' -> R.string.keyword_4
                '5' -> R.string.keyword_5
                '6' -> R.string.keyword_6
                '7' -> R.string.keyword_7
                '8' -> R.string.keyword_8
                '9' -> R.string.keyword_9
                else -> 0
            }
            if (res != 0) it to res else null
        }

    // =====================================================
    // GET DIGIT INTRODUCTION
    // =====================================================
    fun getDigitIntroResIds(number: String) =
        number.groupingBy { it }.eachCount().mapNotNull { (digit, _) ->
            val res = when (digit) {
                '0' -> R.string.digit_0_intro
                '1' -> R.string.digit_1_intro
                '2' -> R.string.digit_2_intro
                '3' -> R.string.digit_3_intro
                '4' -> R.string.digit_4_intro
                '5' -> R.string.digit_5_intro
                '6' -> R.string.digit_6_intro
                '7' -> R.string.digit_7_intro
                '8' -> R.string.digit_8_intro
                '9' -> R.string.digit_9_intro
                else -> 0
            }
            if (res != 0) digit to res else null
        }

    // =====================================================
    // ANGEL IMPULSE FROM REDUCED
    // =====================================================
    fun getAngelImpulseFromReduced(reduced: Int): Int {
        return when (reduced) {
            0 -> R.string.angel_0_1
            1 -> R.string.angel_1_1
            2 -> R.string.angel_2_1
            3 -> R.string.angel_3_1
            4 -> R.string.angel_4_1
            5 -> R.string.angel_5_1
            6 -> R.string.angel_6_1
            7 -> R.string.angel_7_1
            8 -> R.string.angel_8_1
            9 -> R.string.angel_9_1
            11 -> R.string.angel_1_2
            22 -> R.string.angel_2_2
            33 -> R.string.angel_3_2
            44 -> R.string.angel_4_2
            55 -> R.string.angel_5_2
            66 -> R.string.angel_6_2
            77 -> R.string.angel_7_2
            88 -> R.string.angel_8_2
            99 -> R.string.angel_9_2
            else -> 0
        }
    }

    // =====================================================
    // GET ANGEL RES ID'S
    // =====================================================
    fun getAngelResIds(number: String): List<Int> {
        val results = mutableSetOf<Int>()
        if (number.length < 2) return emptyList()
        detectRuns(number, results)  // Run Detection (22, 333, 4444...)
        detectSequences(number, results) // Ascending / Descending Sequences
        detectMirror(number, results) // Mirror / Palindrome
        detectAlternating(number, results) // Alternating Patterns (1212, 2323...)
        return results.toList()
    }

    // =====================================================
    // DETECT RUNS
    // =====================================================
    private fun detectRuns(number: String, results: MutableSet<Int>) {
        var i = 0
        while (i < number.length) {
            val digit = number[i]
            var run = 1
            while (i + run < number.length && number[i + run] == digit) {
                run++
            }

            if (run >= 2) {
                val res = when (digit) {
                    '0' -> angelZero(run)
                    '1' -> angelOne(run)
                    '2' -> angelTwo(run)
                    '3' -> angelThree(run)
                    '4' -> angelFour(run)
                    '5' -> angelFive(run)
                    '6' -> angelSix(run)
                    '7' -> angelSeven(run)
                    '8' -> angelEight(run)
                    '9' -> angelNine(run)
                    else -> 0
                }
                if (res != 0) results.add(res)
            }

            i += run
        }
    }

    // =====================================================
    // ASCENDING / DESCENDING SEQUENCES
    // =====================================================
    private fun detectSequences(number: String, results: MutableSet<Int>) {
        for (start in 0 until number.length - 2) {
            for (end in start + 3..number.length) {
                val sub = number.substring(start, end)
                val digits = sub.map { it.digitToInt() }
                val ascending = digits.zipWithNext().all { it.second == it.first + 1 }
                val descending = digits.zipWithNext().all { it.second == it.first - 1 }

                if (ascending) {
                    results.add(
                        when (sub) {
                            "012" -> R.string.angel_012
                            "123" -> R.string.angel_123
                            "234" -> R.string.angel_234
                            "345" -> R.string.angel_345
                            "456" -> R.string.angel_456
                            "567" -> R.string.angel_567
                            "678" -> R.string.angel_678
                            "789" -> R.string.angel_789
                            "0123" -> R.string.angel_0123
                            "1234" -> R.string.angel_1234
                            "2345" -> R.string.angel_2345
                            "3456" -> R.string.angel_3456
                            "4567" -> R.string.angel_4567
                            "5678" -> R.string.angel_5678
                            "6789" -> R.string.angel_6789
                            "01234" -> R.string.angel_01234
                            "12345" -> R.string.angel_12345
                            "23456" -> R.string.angel_23456
                            "34567" -> R.string.angel_34567
                            "45678" -> R.string.angel_45678
                            "56789" -> R.string.angel_56789
                            "012345" -> R.string.angel_012345
                            "123456" -> R.string.angel_123456
                            "234567" -> R.string.angel_234567
                            "345678" -> R.string.angel_345678
                            "456789" -> R.string.angel_456789
                            else -> R.string.angel_sequence_ascending
                        }
                    )
                }

                if (descending) {
                    results.add(
                        when (sub) {
                            "210" -> R.string.angel_210
                            "321" -> R.string.angel_321
                            "432" -> R.string.angel_432
                            "543" -> R.string.angel_543
                            "654" -> R.string.angel_654
                            "765" -> R.string.angel_765
                            "876" -> R.string.angel_876
                            "987" -> R.string.angel_987
                            "3210" -> R.string.angel_3210
                            "4321" -> R.string.angel_4321
                            "5432" -> R.string.angel_5432
                            "6543" -> R.string.angel_6543
                            "7654" -> R.string.angel_7654
                            "8765" -> R.string.angel_8765
                            "9876" -> R.string.angel_9876
                            "43210" -> R.string.angel_43210
                            "54321" -> R.string.angel_54321
                            "65432" -> R.string.angel_65432
                            "76543" -> R.string.angel_76543
                            "87654" -> R.string.angel_87654
                            "98765" -> R.string.angel_98765
                            "543210" -> R.string.angel_543210
                            "654321" -> R.string.angel_654321
                            "765432" -> R.string.angel_765432
                            "876543" -> R.string.angel_876543
                            "987654" -> R.string.angel_987654
                            else -> R.string.angel_sequence_descending
                        }
                    )
                }
            }
        }
    }

    // =====================================================
    // DETECT MIRROR / PALINDROME
    // =====================================================
    private fun detectMirror(number: String, results: MutableSet<Int>) {
        if (number.length !in 4..6 || !number.all { it.isDigit() }) return
        // 1. Complete palindrome
        if (number == number.reversed()) {
            results.add(R.string.angel_palindrome)
            return
        }
        // 2. Alternating pattern ABAB / ABABAB
        val alternating = number.length >= 4 && number[0] != number[1] && number.indices.all { index -> number[index] == number[index % 2] }

        if (alternating) {
            results.add(R.string.angel_mirror_exchange)
            return
        }
        // 3. Double center
        val middle = number.length / 2
        val doubleCenter = number.length % 2 == 0 && number[middle - 1] == number[middle]

        if (doubleCenter) {
            results.add(R.string.angel_mirror_center)
            return
        }
        // 4. Outer frame
        if (number.first() == number.last()) {
            results.add(R.string.angel_mirror_frame)
        }
    }

    // =====================================================
    // ALTERNATING PATTERN
    // =====================================================
    private fun detectAlternating(number: String, results: MutableSet<Int>) {
        if (number.length >= 4) {
            val pattern = number.substring(0, 2)
            val repeated = pattern.repeat(number.length / 2)
            if (number == repeated) {
                results.add(R.string.angel_alternating)
            }
        }
    }

    private fun angelZero(r: Int) = when (r) {
        1 -> R.string.angel_0_1; 2 -> R.string.angel_0_2; 3 -> R.string.angel_0_3; 4 -> R.string.angel_0_4; 5 -> R.string.angel_0_5; 6 -> R.string.angel_0_6; else -> 0
    }

    private fun angelOne(r: Int) = when (r) {
        1 -> R.string.angel_1_1; 2 -> R.string.angel_1_2; 3 -> R.string.angel_1_3; 4 -> R.string.angel_1_4; 5 -> R.string.angel_1_5; 6 -> R.string.angel_1_6; else -> 0
    }

    private fun angelTwo(r: Int) = when (r) {
        1 -> R.string.angel_1_2; 2 -> R.string.angel_2_2; 3 -> R.string.angel_2_3; 4 -> R.string.angel_2_4; 5 -> R.string.angel_2_5; 6 -> R.string.angel_2_6; else -> 0
    }

    private fun angelThree(r: Int) = when (r) {
        1 -> R.string.angel_3_1; 2 -> R.string.angel_3_2; 3 -> R.string.angel_3_3; 4 -> R.string.angel_3_4; 5 -> R.string.angel_3_5; 6 -> R.string.angel_3_6; else -> 0
    }

    private fun angelFour(r: Int) = when (r) {
        1 -> R.string.angel_4_1; 2 -> R.string.angel_4_2; 3 -> R.string.angel_4_3; 4 -> R.string.angel_4_4; 5 -> R.string.angel_4_5; 6 -> R.string.angel_4_6; else -> 0
    }

    private fun angelFive(r: Int) = when (r) {
        1 -> R.string.angel_5_1; 2 -> R.string.angel_5_2; 3 -> R.string.angel_5_3; 4 -> R.string.angel_5_4; 5 -> R.string.angel_5_5; 6 -> R.string.angel_5_6; else -> 0
    }

    private fun angelSix(r: Int) = when (r) {
        1 -> R.string.angel_6_1; 2 -> R.string.angel_6_2; 3 -> R.string.angel_6_3; 4 -> R.string.angel_6_4; 5 -> R.string.angel_6_5; 6 -> R.string.angel_6_6; else -> 0
    }

    private fun angelSeven(r: Int) = when (r) {
        1 -> R.string.angel_7_1; 2 -> R.string.angel_7_2; 3 -> R.string.angel_7_3; 4 -> R.string.angel_7_4; 5 -> R.string.angel_7_5; 6 -> R.string.angel_7_6; else -> 0
    }

    private fun angelEight(r: Int) = when (r) {
        1 -> R.string.angel_8_1; 2 -> R.string.angel_8_2; 3 -> R.string.angel_8_3; 4 -> R.string.angel_8_4; 5 -> R.string.angel_8_5; 6 -> R.string.angel_8_6; else -> 0
    }

    private fun angelNine(r: Int) = when (r) {
        1 -> R.string.angel_9_1; 2 -> R.string.angel_9_2; 3 -> R.string.angel_9_3; 4 -> R.string.angel_9_4; 5 -> R.string.angel_9_5; 6 -> R.string.angel_9_6; else -> 0
    }

    // =====================================================
    // MASTER / DOMINANT
    // =====================================================
    fun getMasterCore(sum: Int, reduced: Int) = when {
        sum in masterCoreNumbers -> sum
        reduced in masterCoreNumbers -> reduced
        else -> null
    }

    // =====================================================
    // MASTER AMPLIFIERS
    // =====================================================
    fun getMasterAmplifiers(number: String) =
        masterAmplifierNumbers.filter { number.contains(it.toString()) }

    // =====================================================
    // ORDERED MASTER NUMBERS
    // =====================================================
    fun getOrderedMasters(
        number: String,
        crossReduced: Int,
        masterCore: Int?,
        masterAmplifiers: List<Int>
    ): List<Int> {

        val result = mutableListOf<Int>()
        // 1. Meisterzahl aus Quersumme zuerst
        if (crossReduced in masterCoreNumbers) {
            result.add(crossReduced)
        }
        // 2. Core falls noch nicht enthalten
        masterCore?.let { if (!result.contains(it)) result.add(it) }
        // 3. Amplifier nach Auftreten im String sortieren
        masterAmplifiers
            .distinct()
            .sortedBy { number.indexOf(it.toString()) }
            .forEach {
                if (!result.contains(it)) result.add(it)
            }

        return result
    }

    fun getDominantRepeatDigit(number: String): Int? {
        val dominant = number.groupingBy { it }.eachCount().maxByOrNull { it.value }
        return if (dominant != null && dominant.value >= 3) dominant.key.digitToInt() else null
    }

    // =====================================================
    // FREQUENCY
    // =====================================================
    fun calculateFrequencyScore(number: String): Float {
        if (number.isEmpty()) return 0f
        val avg = number.map { it.digitToInt() }.average() / 9.0
        return avg.coerceIn(0.1, 1.0).toFloat()
    }

    // --------------------------------------------------
    // CONSTANT COLOR FREQUENCY
    // --------------------------------------------------
    fun frequencyColor(percent: Float): Color {
        val clamped = percent.coerceIn(0f, 1f)
        val hue = clamped * 120f
        return Color.hsv(
            hue = hue,
            saturation = 1f,
            value = 1f
        )
    }

    // =====================================================
    // STATE LOGIC BY FREQUENCY
    // =====================================================
    fun getStateResId(frequency: Float): Int {
        return when {
            frequency >= 0.95f -> R.string.state_pure_clarity_short
            frequency >= 0.90f -> R.string.state_clarity_strong_short
            frequency >= 0.85f -> R.string.state_clarity_medium_short
            frequency >= 0.80f -> R.string.state_clarity_weak_short
            frequency >= 0.75f -> R.string.state_alignment_strong_short
            frequency >= 0.70f -> R.string.state_alignment_weak_short
            frequency >= 0.65f -> R.string.state_tension_strong_short
            frequency >= 0.60f -> R.string.state_tension_weak_short
            frequency >= 0.55f -> R.string.state_focus_strong_short
            frequency >= 0.50f -> R.string.state_focus_weak_short
            frequency >= 0.45f -> R.string.state_shift_strong_short
            frequency >= 0.40f -> R.string.state_shift_weak_short
            frequency >= 0.35f -> R.string.state_transformation_strong_short
            frequency >= 0.30f -> R.string.state_transformation_weak_short
            frequency >= 0.20f -> R.string.state_reflection_short
            frequency >= 0.15f -> R.string.state_open_short
            frequency >= 0.10f -> R.string.state_withdrawal_short
            frequency >= 0.05f -> R.string.state_fragile_short
            else -> R.string.state_void_short
        }
    }

    // =====================================================
    // BUILD HARMONY TEXT
    // =====================================================
    fun buildHarmonyText(
        context: Context,
        reduced: Int,
        amplifiers: List<Int>,
        frequency: Float
    ): Pair<String, String> {

        val harmony = frequency >= 0.5f
        val title = context.getString(if (harmony) R.string.harmony_title else R.string.learning_title)
        val values = (listOf(reduced) + amplifiers).distinct()
        val text = values.mapNotNull { value ->
            val key = if (harmony) "harmony_$value" else "learning_$value"
            val resId = context.resources.getIdentifier(key, "string", context.packageName)

            if (resId != 0) {
                "$value: ${context.getString(resId)}"
            } else {
                null
            }
        }.joinToString("\n\n")
        return Pair(title, text)
    }

    // =====================================================
    // PSEUDO HZ FORMATTED
    // =====================================================
    fun getPseudoHzFormatted(frequency: Float): String {
        val safeFrequency = frequency.coerceIn(0.1f, 1.0f)
        val hz = safeFrequency * 900f + 100f
        return "~${kotlin.math.round(hz).toInt()}Hz"
    }

    // =====================================================
    // GET BATTERY LEVEL
    // =====================================================
    fun getBatteryLevel(context: Context): Int {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        return if (level != -1 && scale != -1) {
            (level * 100 / scale.toFloat()).toInt()
        } else {
            0
        }
    }

    // =====================================================
    // GET CHAKRA NAME - STEP 1
    // =====================================================
    fun getChakraNameResId(value: Int): Int =
        when (value) {
            1 -> R.string.chakra_root
            2 -> R.string.chakra_sacral
            3 -> R.string.chakra_solar
            4 -> R.string.chakra_heart
            5 -> R.string.chakra_throat
            6 -> R.string.chakra_third_eye
            7 -> R.string.chakra_crown
            8 -> R.string.chakra_root
            9 -> R.string.chakra_crown
            11 -> R.string.chakra_third_eye
            22 -> R.string.chakra_root
            33 -> R.string.chakra_heart
            44 -> R.string.chakra_throat
            55 -> R.string.chakra_solar
            66 -> R.string.chakra_sacral
            77 -> R.string.chakra_crown
            88 -> R.string.chakra_root
            99 -> R.string.chakra_crown
            else -> R.string.chakra_crown
        }

    // =====================================================
    // BUILD CHAKRA TEXT - STEP 2
    // =====================================================
    fun buildChakraText(context: Context, reduced: Int, amplifiers: List<Int>): String {

        val values = (listOf(reduced) + amplifiers).distinct()
        return values.map { value ->
            val chakraName = context.getString(getChakraNameResId(value))
            val messageKey = "chakra_${value}_message"
            val messageResId = context.resources.getIdentifier(messageKey, "string", context.packageName)
            if (messageResId != 0) {
                val message = context.getString(messageResId)
                "$value: $chakraName – $message"
            } else {
                "$value: $chakraName"
            }
        }.joinToString("\n\n")
    }

    // =====================================================
    // GET PERSONAL RESONANCE CHAKRA BALANCE
    // =====================================================
    fun getPersonalResonanceChakraBalance(reduced: Int, amplifiers: List<Int>): List<FibonacciChakraBalance> {
        val chakraValues = listOf(1, 2, 3, 4, 5, 6, 7)
        val now = java.util.Calendar.getInstance()
        val year = now.get(java.util.Calendar.YEAR)
        val dayOfYear = now.get(java.util.Calendar.DAY_OF_YEAR)
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = now.get(java.util.Calendar.MINUTE)
        val second = now.get(java.util.Calendar.SECOND)
        val timeValue = year.toDouble() * 365.2425 + dayOfYear.toDouble() + hour.toDouble() / 24.0 + minute.toDouble() / 1440.0 + second.toDouble() / 86400.0
        val phi = 1.618033988749895
        val goldenWeights = listOf(1.0 / phi, 1.0, phi, 1.0, phi, 1.0, 1.0 / phi)
        val goldenTotal = goldenWeights.sum()
        val fibonacciTotal = fibonacciChakraWeights.sum().toDouble()
        val fibonacciRatio = 0.70
        val goldenRatioWeight = 0.30
        val targetWeights = chakraValues.mapIndexed { index, _ ->
            val fibonacci = fibonacciChakraWeights[index].toDouble() / fibonacciTotal
            val golden = goldenWeights[index] / goldenTotal
            fibonacci * fibonacciRatio + golden * goldenRatioWeight
        }

        val targetTotal = targetWeights.sum()
        val values = (listOf(reduced) + amplifiers).distinct()
        val activation = IntArray(7)
        values.forEach { value ->
            when (getChakraNameResId(value)) {
                R.string.chakra_root -> activation[0]++
                R.string.chakra_sacral -> activation[1]++
                R.string.chakra_solar -> activation[2]++
                R.string.chakra_heart -> activation[3]++
                R.string.chakra_throat -> activation[4]++
                R.string.chakra_third_eye -> activation[5]++
                R.string.chakra_crown -> activation[6]++
            }
        }

        val totalActivation = activation.sum().coerceAtLeast(1).toDouble()
        val resonanceValue = values.sum().toDouble()
        return chakraValues.mapIndexed { index, chakraValue ->
            val targetPercent = targetWeights[index] / targetTotal * 100.0
            val activationPercent = activation[index].toDouble() / totalActivation * 100.0
            val frequency = phi + (index + 1) * 0.1618033988749895
            val phase = timeValue * frequency + resonanceValue * phi
            val wave = kotlin.math.abs(kotlin.math.sin(phase))
            val resonanceInfluence = (1.0 + kotlin.math.sin(resonanceValue * (index + 1) * 0.618033988749895)) / 2.0
            val personalState = activationPercent * 0.40 + wave * 100.0 * 0.35 + resonanceInfluence * 100.0 * 0.25
            val difference = kotlin.math.abs(personalState - targetPercent)
            val normalizedDifference = (difference / 100.0).coerceIn(0.0, 1.0)
            val harmonyFactor = kotlin.math.exp(-2.0 * normalizedDifference)
            val balancePercent = (harmonyFactor * 100.0).coerceIn(0.0, 100.0).toInt()
            val status = when {
                balancePercent >= 80 -> BalanceStatus.BALANCED
                balancePercent >= 50 -> BalanceStatus.ADJUSTMENT
                else -> BalanceStatus.IMBALANCED
            }

            FibonacciChakraBalance(
                chakraValue = chakraValue,
                nameResId = getChakraNameResId(chakraValue),
                fibonacciWeight = fibonacciChakraWeights[index],
                balancePercent = balancePercent,
                status = status
            )
        }
    }

    // =====================================================
    // ENERGY FLOW
    // =====================================================
    fun getEnergyFlowResId(masterCore: Int?, dominant: Int?, reduced: Int): Int {
        masterCore?.let {
            return when (it) {
                11 -> R.string.energy_11
                22 -> R.string.energy_22
                33 -> R.string.energy_33
                44 -> R.string.energy_44
                55 -> R.string.energy_55
                66 -> R.string.energy_66
                77 -> R.string.energy_77
                88 -> R.string.energy_88
                99 -> R.string.energy_99
                else -> R.string.energy_default
            }
        }

        dominant?.let {
            return when (it) {
                0 -> R.string.energy_repeat_0
                1 -> R.string.energy_repeat_1
                2 -> R.string.energy_repeat_2
                3 -> R.string.energy_repeat_3
                4 -> R.string.energy_repeat_4
                5 -> R.string.energy_repeat_5
                6 -> R.string.energy_repeat_6
                7 -> R.string.energy_repeat_7
                8 -> R.string.energy_repeat_8
                9 -> R.string.energy_repeat_9
                else -> R.string.energy_default
            }
        }

        return when (reduced) {
            0 -> R.string.energy_0
            1 -> R.string.energy_1
            2 -> R.string.energy_2
            3 -> R.string.energy_3
            4 -> R.string.energy_4
            5 -> R.string.energy_5
            6 -> R.string.energy_6
            7 -> R.string.energy_7
            8 -> R.string.energy_8
            9 -> R.string.energy_9
            else -> R.string.energy_default
        }
    }

    // =====================================================
    // RESONANCE
    // =====================================================
    public fun getResonance(number: String): Triple<Int, Int, Int>? {
        if (number.length != 4 && number.length != 6) return null
        if (!number.all { it.isDigit() }) return null

        val d = number.toCharArray()
        val length = number.length
        // 4-DIGIT PATTERNS
        fun isMono4() = length == 4 && d.all { it == d[0] }
        fun isAABB() = length == 4 && d[0] == d[1] && d[2] == d[3] && d[0] != d[2]
        fun isABAB() = length == 4 && d[0] == d[2] && d[1] == d[3] && d[0] != d[1]
        fun isABBA() = length == 4 && d[0] == d[3] && d[1] == d[2] && d[0] != d[1]
        fun isABCD() = length == 4 && number.toSet().size == 4
        // 6-DIGIT PATTERNS
        fun isMono6() = length == 6 && d.all { it == d[0] }
        fun isAAABBB() = length == 6 && d[0] == d[1] && d[1] == d[2] && d[3] == d[4] && d[4] == d[5] && d[0] != d[3]
        fun isAABBCC() = length == 6 && d[0] == d[1] && d[2] == d[3] && d[4] == d[5] && d[0] != d[2] && d[0] != d[4] && d[2] != d[4]
        fun isABABAB() = length == 6 && d[0] == d[2] && d[2] == d[4] && d[1] == d[3] && d[3] == d[5] && d[0] != d[1]
        fun isABCABC() = length == 6 && number.substring(0, 3) == number.substring(3, 6)
        fun isMirror6() = length == 6 && number == number.reversed()
        // 4-DIGIT RESONANCE
        if (length == 4) {
            return when {
                isMono4() -> Triple(R.string.resonance4_title_mono, R.string.resonance4_meaning_mono, R.string.resonance_focus_none)
                isAABB() -> Triple(R.string.resonance4_title_aabb, R.string.resonance4_meaning_aabb, R.string.resonance_focus_frame)
                isABAB() -> Triple(R.string.resonance4_title_abab, R.string.resonance4_meaning_abab, R.string.resonance_focus_center)
                isABBA() -> Triple(R.string.resonance4_title_abba, R.string.resonance4_meaning_abba, R.string.resonance_focus_center)
                isABCD() -> Triple(R.string.resonance4_title_abcd, R.string.resonance4_meaning_abcd, R.string.resonance_focus_none)
                else -> Triple(R.string.resonance4_title_none, R.string.resonance4_meaning_none, R.string.resonance_focus_none)
            }
        }
        // 6-DIGIT RESONANCE
        return when {
            isMono6() -> Triple(R.string.resonance6_title_mono, R.string.resonance6_meaning_mono, R.string.resonance_focus_none)
            isAAABBB() -> Triple(R.string.resonance6_title_aaabbb, R.string.resonance6_meaning_aaabbb, R.string.resonance_focus_frame)
            isAABBCC() -> Triple(R.string.resonance6_title_aabbcc, R.string.resonance6_meaning_aabbcc, R.string.resonance_focus_start)
            isABABAB() -> Triple(R.string.resonance6_title_ababab, R.string.resonance6_meaning_ababab, R.string.resonance_focus_center)
            isABCABC() -> Triple(R.string.resonance6_title_abcabc, R.string.resonance6_meaning_abcabc, R.string.resonance_focus_center)
            isMirror6() -> Triple(R.string.resonance6_title_mirror, R.string.resonance6_meaning_mirror, R.string.resonance_focus_center)
            else -> Triple(R.string.resonance6_title_none, R.string.resonance6_meaning_none, R.string.resonance_focus_none)
        }
    }

    // =====================================================
    // TIME ENERGY MESSAGE
    // =====================================================
    fun getTimeEnergyMessage(context: Context, currentTime: String): String? {
        val normalized = currentTime.replace(":", "").trim()
        val resName = "time_$normalized"
        val resId = context.resources.getIdentifier(resName, "string", context.packageName)
        return if (resId != 0) {
            context.getString(resId)
        } else {
            null
        }
    }

    // =====================================================
    // TIME FORMATTER
    // =====================================================
    private val timeFormatter by lazy {
        java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    }

    // =====================================================
    // BUILD RESULT
    // =====================================================
    fun buildEngineResult(number: String, context: Context? = null): EngineResult {
        val cross = calculateCrossSum(number)
        val reduced = cross.reducedSum
        val masterCore = getMasterCore(cross.sumBeforeReduce, reduced)
        val amplifiers = getMasterAmplifiers(number)
        val dominant = getDominantRepeatDigit(number)
        val frequency = calculateFrequencyScore(number)
        val harmonyInfo = context?.let { buildHarmonyText(context = it, reduced = reduced, amplifiers = amplifiers, frequency = frequency) }
        val harmonyTitle = harmonyInfo?.first ?: ""
        val harmonyText = harmonyInfo?.second ?: ""
        val chakraTitle = context?.getString(R.string.section_chakra_energy) ?: ""
        val chakraText = context?.let { buildChakraText(context = it, reduced = reduced, amplifiers = amplifiers) } ?: ""
        val personalChakraBalance = getPersonalResonanceChakraBalance( reduced = reduced, amplifiers = amplifiers )
        val frequencyHz = getPseudoHzFormatted(frequency)
        val stateResId = getStateResId(frequency)
        val resonance = getResonance(number)
        val summaryResId = getSummaryResId(masterCore, amplifiers, dominant, reduced)
        val currentTime = timeFormatter.format(java.util.Date())
        val timeMessage = context?.let { getTimeEnergyMessage(context = it, currentTime = currentTime) }
        val batteryLevel = context?.let { getBatteryLevel(it) } ?: 0
        val batteryTitle = context?.getString(R.string.section_battery_energy) + " $batteryLevel%"
        val batteryMessage = context?.let {
            val resId = it.resources.getIdentifier("battery_$batteryLevel", "string", it.packageName)
            if (resId != 0) { it.getString(resId) } else { "" }
        } ?: ""

        return EngineResult(
            number,
            cross,
            getNumerologyTitleResId(reduced),
            getDigitKeywords(number),
            getDigitIntroResIds(number),
            getAngelResIds(number),
            angelImpulseResId = getAngelImpulseFromReduced(reduced),
            masterCore,
            amplifiers,
            dominant,
            frequency,
            getEnergyFlowResId(masterCore, dominant, reduced),
            summaryResId,
            resonance?.first,
            resonance?.second,
            resonance?.third,
            getKarmicDetailResId(number),
            stateResId,
            frequencyHz,
            currentTime,
            timeMessage,
            chakraTitle = chakraTitle,
            chakraText = chakraText,
            personalChakraBalance = personalChakraBalance,
            batteryLevel = batteryLevel,
            batteryTitle = batteryTitle,
            batteryMessage = batteryMessage,
            harmonyTitle = harmonyTitle,
            harmonyText = harmonyText
        )
    }

    fun getSummaryResId(masterCore: Int?, amps: List<Int>, dom: Int?, reduced: Int) =
        when {
            masterCore != null -> when (masterCore) {
                11 -> R.string.summary_master_11
                22 -> R.string.summary_master_22
                33 -> R.string.summary_master_33
                44 -> R.string.summary_master_44
                55 -> R.string.summary_master_55
                66 -> R.string.summary_master_66
                77 -> R.string.summary_master_77
                88 -> R.string.summary_master_88
                99 -> R.string.summary_master_99
                else -> R.string.summary_default
            }

            amps.size >= 2 -> R.string.summary_multi_master
            dom != null -> when (dom) {
                0 -> R.string.summary_repeat_0
                1 -> R.string.summary_repeat_1
                2 -> R.string.summary_repeat_2
                3 -> R.string.summary_repeat_3
                4 -> R.string.summary_repeat_4
                5 -> R.string.summary_repeat_5
                6 -> R.string.summary_repeat_6
                7 -> R.string.summary_repeat_7
                8 -> R.string.summary_repeat_8
                9 -> R.string.summary_repeat_9
                else -> R.string.summary_default
            }

            else -> when (reduced) {
                1 -> R.string.summary_1
                2 -> R.string.summary_2
                3 -> R.string.summary_3
                4 -> R.string.summary_4
                5 -> R.string.summary_5
                6 -> R.string.summary_6
                7 -> R.string.summary_7
                8 -> R.string.summary_8
                9 -> R.string.summary_9
                else -> R.string.summary_default
            }
        }

    // Get karmic detail
    fun getKarmicDetailResId(number: String): Int? {
        val sum = calculateCrossSum(number).sumBeforeReduce
        return when (sum) {
            1 -> R.string.karmic_detail_1
            2 -> R.string.karmic_detail_2
            3 -> R.string.karmic_detail_3
            4 -> R.string.karmic_detail_4
            5 -> R.string.karmic_detail_5
            6 -> R.string.karmic_detail_6
            7 -> R.string.karmic_detail_7
            8 -> R.string.karmic_detail_8
            9 -> R.string.karmic_detail_9
            10 -> R.string.karmic_detail_10
            11 -> R.string.karmic_detail_11
            12 -> R.string.karmic_detail_12
            13 -> R.string.karmic_detail_13
            14 -> R.string.karmic_detail_14
            15 -> R.string.karmic_detail_15
            16 -> R.string.karmic_detail_16
            17 -> R.string.karmic_detail_17
            18 -> R.string.karmic_detail_18
            19 -> R.string.karmic_detail_19
            20 -> R.string.karmic_detail_20
            21 -> R.string.karmic_detail_21
            22 -> R.string.karmic_detail_22
            23 -> R.string.karmic_detail_23
            24 -> R.string.karmic_detail_24
            25 -> R.string.karmic_detail_25
            26 -> R.string.karmic_detail_26
            27 -> R.string.karmic_detail_27
            28 -> R.string.karmic_detail_28
            29 -> R.string.karmic_detail_29
            30 -> R.string.karmic_detail_30
            31 -> R.string.karmic_detail_31
            32 -> R.string.karmic_detail_32
            33 -> R.string.karmic_detail_33
            else -> null
        }
    }

    // Get special meaning
    fun getSpecialMeaningResId(value: Int): Int? {
        return when (value) {
            11 -> R.string.master_number_11
            22 -> R.string.master_number_22
            33 -> R.string.master_number_33
            44 -> R.string.master_number_44
            55 -> R.string.special_number_55
            66 -> R.string.special_number_66
            77 -> R.string.special_number_77
            88 -> R.string.special_number_88
            99 -> R.string.special_number_99
            else -> null
        }
    }

    // Copy-Renderer
    fun EngineResult.buildVisibleCopyText(context: Context): String {

        val builder = StringBuilder()
        // --------------------------------------------------
        // TITLE
        // --------------------------------------------------
        builder.appendLine(context.getString(numerologyTitleResId))
        builder.appendLine()
        // --------------------------------------------------
        // MESSAGE FOR THE MOMENT
        // --------------------------------------------------
        builder.appendLine(context.getString(R.string.section_message_for_the_moment, number))
        builder.appendLine()
        // --------------------------------------------------
        // CROSS SUM
        // --------------------------------------------------
        builder.appendLine(context.getString(R.string.your_cross_sum_is))
        builder.appendLine(crossSum.fullCalculationText)
        builder.appendLine()
        // --------------------------------------------------
        // MASTER ENERGY
        // --------------------------------------------------
        val masterTexts = mutableListOf<String>()
        val shown = mutableSetOf<Int>()

        masterCore?.let { core ->
            getSpecialMeaningResId(core)?.let { resId ->
                masterTexts.add(context.getString(resId).cleanMarkdown())
                shown.add(core)
            }
        }

        masterAmplifiers.forEach { amp ->
            if (!shown.contains(amp)) {
                getSpecialMeaningResId(amp)?.let { resId ->
                    masterTexts.add(context.getString(resId).cleanMarkdown())
                }
            }
        }
        if (masterTexts.isNotEmpty()) {
            builder.appendLine(context.getString(R.string.section_master_energy))
            masterTexts.forEach { builder.appendLine(it) }
            builder.appendLine()
        }
        // -------------------------------------------------
        // ANGEL SECTION
        // -------------------------------------------------
        if (angelImpulseResId != 0 || angelResIds.isNotEmpty()) {

            builder.appendLine(context.getString(R.string.section_angel))
            val shownAngelIds = mutableSetOf<Int>()

            if (angelImpulseResId != 0) {
                shownAngelIds.add(angelImpulseResId)
                builder.appendLine(context.getString(angelImpulseResId).cleanMarkdown())
            }

            angelResIds
                .distinct()
                .forEach { resId ->

                    if (!shownAngelIds.contains(resId)) {
                        shownAngelIds.add(resId)
                        builder.appendLine(
                            context.getString(resId).cleanMarkdown()
                        )
                    }
                }

            builder.appendLine()
        }
        // --------------------------------------------------
        // CHAKRA
        // --------------------------------------------------
        builder.appendLine(chakraTitle)
        builder.appendLine(chakraText.lines().filter { it.isNotBlank() }.joinToString("\n"))
        builder.appendLine()
        // --------------------------------------------------
        // HARMONY / LEARNING
        // --------------------------------------------------
        builder.appendLine(harmonyTitle)
        builder.appendLine(harmonyText.lines().filter { it.isNotBlank() }.joinToString("\n"))
        builder.appendLine()
        // --------------------------------------------------
        // CHAKRA FIBONACCI BALANCE
        // --------------------------------------------------
        builder.appendLine(context.getString(R.string.section_personal_balance))
            personalChakraBalance.forEach { chakra ->
            val name = context.getString(chakra.nameResId)
            val icon = when (chakra.status) {
                BalanceStatus.BALANCED -> "🟢"
                BalanceStatus.ADJUSTMENT -> "🟡"
                BalanceStatus.IMBALANCED -> "🔴"
            }

            builder.appendLine("$icon $name: ${chakra.balancePercent}%")
        }

        builder.appendLine()
        // -------------------------------------------------
        // BATTERY ENERGY
        // -------------------------------------------------
        builder.appendLine(batteryTitle)
        builder.appendLine(batteryMessage)
        // -------------------------------------------------
        // TIME ENERGY
        // -------------------------------------------------
        builder.appendLine()
        timeEnergyMessage?.let {
            builder.appendLine("${context.getString(R.string.section_time_energy)} $currentTime")
            builder.appendLine(it.cleanMarkdown())
            builder.appendLine()
        }
        // --------------------------------------------------
        // FREQUENCY
        // --------------------------------------------------
        val percent = (frequencyScore * 100).toInt()
        builder.appendLine("${context.getString(R.string.section_frequency)} ${getPseudoHzFormatted(frequencyScore)} / $percent%")
        builder.appendLine(context.getString(stateResId).cleanMarkdown())
        builder.appendLine()
        // --------------------------------------------------
        // DIGIT VIBRATION
        // --------------------------------------------------
        val uniqueDigits: List<Char> = number.filter { it.isDigit() }.toList().distinct()

        if (uniqueDigits.isNotEmpty()) {
            builder.appendLine(context.getString(R.string.section_vibration))
            uniqueDigits.forEach { digit: Char ->
                val res = when (digit) {
                    '0' -> R.string.keyword_0
                    '1' -> R.string.keyword_1
                    '2' -> R.string.keyword_2
                    '3' -> R.string.keyword_3
                    '4' -> R.string.keyword_4
                    '5' -> R.string.keyword_5
                    '6' -> R.string.keyword_6
                    '7' -> R.string.keyword_7
                    '8' -> R.string.keyword_8
                    '9' -> R.string.keyword_9
                    else -> null
                }

                res?.let {
                    builder.appendLine(
                        "$digit – ${context.getString(it).cleanMarkdown()}"
                    )
                }
            }

            builder.appendLine()
        }
        // --------------------------------------------------
        // DIGIT DESCRIPTION
        // --------------------------------------------------
        val counts = number.groupingBy { it }.eachCount()

        if (digitIntroResIds.isNotEmpty()) {
            builder.appendLine(context.getString(R.string.section_description_numbers))
            digitIntroResIds.forEach { (digit, resId) ->

                val count = counts[digit] ?: 1
                val amplifyText =
                    if (count >= 2)
                        context.getString(R.string.digit_occurrence, count)
                    else ""

                val formatted = context.getString(resId, " $amplifyText")
                builder.appendLine(formatted.cleanMarkdown())
                builder.appendLine()
            }
        }
        // --------------------------------------------------
        // RESONANCE
        // --------------------------------------------------
        if (resonanceTitleResId != null) {

            builder.appendLine(context.getString(resonanceTitleResId))

            resonanceMeaningResId?.let {
                builder.appendLine(context.getString(it).cleanMarkdown())
            }

            resonanceFocusResId?.let {
                builder.appendLine(context.getString(it).cleanMarkdown())
            }

            builder.appendLine()
        }
        // --------------------------------------------------
        // SUMMARY
        // --------------------------------------------------
        builder.appendLine(context.getString(R.string.section_summary))
        builder.appendLine(context.getString(summaryResId).cleanMarkdown())
        builder.appendLine()
        // --------------------------------------------------
        // KARMIC DETAIL
        // --------------------------------------------------
        karmicDetailResId?.let {
            builder.appendLine(context.getString(it).cleanMarkdown())
            builder.appendLine()
        }
        // --------------------------------------------------
        // ENERGY FLOW
        // --------------------------------------------------
        builder.appendLine(context.getString(R.string.section_energy))
        builder.appendLine(context.getString(energyFlowResId).cleanMarkdown())
        return builder.toString().trim()
    }
}