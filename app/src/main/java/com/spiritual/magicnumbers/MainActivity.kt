package com.spiritual.magicnumbers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import kotlin.random.Random

// --- DATENKLASSEN ---
data class CrossSumResult(
    val fullCalculationText: String,
    val finalValue: Int
)

fun calculateReducedCrossSum(number: String): CrossSumResult {
    val clean = number.filter { it.isDigit() }

    if (clean.isEmpty()) return CrossSumResult("", 0)

    val steps = mutableListOf<String>()
    // Erste Quersumme
    var current = clean.map { it.digitToInt() }
    var sum = current.sum()
    steps.add(current.joinToString(" + ") + " = $sum")

    // Reduktion (außer bei Meisterzahlen)
    while (sum > 9 && sum !in listOf(11, 22, 33, 44, 55, 66, 77, 88, 99)) {
        val digits = sum.toString().map { it.digitToInt() }
        val newSum = digits.sum()
        steps.add(digits.joinToString(" + ") + " = $newSum")
        sum = newSum
    }

    return CrossSumResult(
        fullCalculationText = steps.joinToString(" → "),
        finalValue = sum
    )
}

data class NumberMeaning(
    val meaning: String,
    val karmicDetail: String? = null
)

data class DetailedMessage(
    val number: String = "",
    val title: String = "",
    val subtitle: String = "",
    val calculationText: String = "",
    val components: List<Pair<Char, String>> = emptyList(),
    val message: String = "",
    val summary: String = "",
    val energyFlow: String = "",
    val frequencyScore: Float = 0f,
    val resonanceId: String? = null // Debug Anzeige only
) {
    fun asFormattedString(
        vibrationHeader: String,
        messageHeader: String,
        summaryHeader: String,
        energyHeader: String,
        frequencyLabel: String
    ): String {
        val componentString = components.joinToString("\n") { (digit, meaning) -> "$digit – $meaning" }
        val frequencyPercent = (frequencyScore * 100).toInt()

        return """
            |$title
            |
            |$calculationText
            |
            |$subtitle
            |
            |🌀 $frequencyLabel: $frequencyPercent%
            |
            |$vibrationHeader
            |$componentString
            |
            |$messageHeader
            |${message.replace("*", "")}
            |
            |$summaryHeader
            |$summary
            |
            |$energyHeader
            |$energyFlow
        """.trimMargin()
    }
}

// --- RESONANZDATEN ---
data class ResonanceInfo(
    val id: String,
    val title: String,
    val shortMeaning: String,
    val focus: String,
    val scoreBoost: Float = 0f
)

// --- MAIN ACTIVITY ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.hashCode()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.hashCode())
        )
        super.onCreate(savedInstanceState)
        setContent { MagicNumberApp() }
    }
}

// --- MARKDOWN-FUNKTIONEN ---
@Composable
fun markdownToAnnotatedString(text: String): AnnotatedString {
    return buildAnnotatedString {
        val parts = text.split("**")
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(part) }
            } else {
                append(part)
            }
        }
    }
}

// --- LOGIK-FUNKTIONEN ---
fun getFrequencyColor(percentage: Float): Color {
    return when {
        percentage < 0.5f -> {
            val p = percentage * 2
            Color(red = 1f, green = p, blue = 0f, alpha = 1f)
        }

        else -> {
            val p = (percentage - 0.5f) * 2
            Color(red = 1f - p, green = 1f, blue = p * 0.5f, alpha = 1f)
        }
    }
}

@Composable
fun getKeywordForDigit(digit: Char): String {
    return when (digit) {
        '0' -> stringResource(R.string.keyword_0)
        '1' -> stringResource(R.string.keyword_1)
        '2' -> stringResource(R.string.keyword_2)
        '3' -> stringResource(R.string.keyword_3)
        '4' -> stringResource(R.string.keyword_4)
        '5' -> stringResource(R.string.keyword_5)
        '6' -> stringResource(R.string.keyword_6)
        '7' -> stringResource(R.string.keyword_7)
        '8' -> stringResource(R.string.keyword_8)
        '9' -> stringResource(R.string.keyword_9)
        else -> ""
    }
}

/* -------------------------------------------------------------
   ANGEL: nur echte "Runs" (adjacent), mehrere Runs möglich
   Beispiel: 117767 -> 11, 77, 67? => nur 11 und 77
------------------------------------------------------------- */
@Composable
fun getAllAngelMessages(number: String): List<String> {
    if (number.isEmpty()) return emptyList()

    val context = LocalContext.current
    val result = mutableListOf<String>()

    var i = 0
    while (i < number.length) {
        val digit = number[i]
        var runLen = 1

        while (i + runLen < number.length && number[i + runLen] == digit) {
            runLen++
        }

        if (runLen >= 2 && digit.isDigit()) {
            val resName = "angel_${digit}_$runLen"
            val resId = context.resources.getIdentifier(resName, "string", context.packageName)
            if (resId != 0) result.add(stringResource(resId))
        }

        i += runLen
    }

    return result.distinct()
}

/* -------------------------------------------------------------
   RESONANZ-LOGIK (6-stellig, Muster-Erkennung)
------------------------------------------------------------- */
@Composable
fun getResonanceInfo6(number: String): ResonanceInfo? {
    if (number.length != 6 || number.any { !it.isDigit() }) return null

    val d = number.toCharArray()

    fun allSame() = d.all { it == d[0] }
    fun isAAABBB() = (d[0] == d[1] && d[1] == d[2]) && (d[3] == d[4] && d[4] == d[5]) && d[0] != d[3]
    fun isAABBCC() = (d[0] == d[1]) && (d[2] == d[3]) && (d[4] == d[5]) && !(d[0] == d[2] && d[2] == d[4])
    fun isABABAB() = (d[0] == d[2] && d[2] == d[4]) && (d[1] == d[3] && d[3] == d[5]) && d[0] != d[1]
    fun isABCABC() = number.substring(0, 3) == number.substring(3, 6)
    fun isPalindrome() = number == number.reversed()

    val pairPositions = (0..4).filter { i -> d[i] == d[i + 1] }
    val focusResId = when {
        pairPositions.isEmpty() -> R.string.resonance_focus_none
        pairPositions.any { it <= 1 } && pairPositions.any { it >= 3 } -> R.string.resonance_focus_frame
        pairPositions.any { it == 2 } -> R.string.resonance_focus_center
        pairPositions.any { it <= 1 } -> R.string.resonance_focus_start
        else -> R.string.resonance_focus_end
    }

    val focus = stringResource(focusResId)

    return when {
        allSame() -> ResonanceInfo(
            id = stringResource(R.string.resonance6_id_mono),
            title = stringResource(R.string.resonance6_title_mono),
            shortMeaning = stringResource(R.string.resonance6_meaning_mono),
            focus = focus,
            scoreBoost = 0.08f
        )

        isAAABBB() -> ResonanceInfo(
            id = stringResource(R.string.resonance6_id_aaabbb),
            title = stringResource(R.string.resonance6_title_aaabbb),
            shortMeaning = stringResource(R.string.resonance6_meaning_aaabbb),
            focus = focus,
            scoreBoost = 0.06f
        )

        isAABBCC() -> ResonanceInfo(
            id = stringResource(R.string.resonance6_id_aabbcc),
            title = stringResource(R.string.resonance6_title_aabbcc),
            shortMeaning = stringResource(R.string.resonance6_meaning_aabbcc),
            focus = focus,
            scoreBoost = 0.05f
        )

        isABABAB() -> ResonanceInfo(
            id = stringResource(R.string.resonance6_id_ababab),
            title = stringResource(R.string.resonance6_title_ababab),
            shortMeaning = stringResource(R.string.resonance6_meaning_ababab),
            focus = focus,
            scoreBoost = 0.05f
        )

        isABCABC() -> ResonanceInfo(
            id = stringResource(R.string.resonance6_id_abcabc),
            title = stringResource(R.string.resonance6_title_abcabc),
            shortMeaning = stringResource(R.string.resonance6_meaning_abcabc),
            focus = focus,
            scoreBoost = 0.04f
        )

        isPalindrome() -> ResonanceInfo(
            id = stringResource(R.string.resonance6_id_mirror),
            title = stringResource(R.string.resonance6_title_mirror),
            shortMeaning = stringResource(R.string.resonance6_meaning_mirror),
            focus = focus,
            scoreBoost = 0.04f
        )

        else -> ResonanceInfo(
            id = stringResource(R.string.resonance6_id_none),
            title = stringResource(R.string.resonance6_title_none),
            shortMeaning = stringResource(R.string.resonance6_meaning_none),
            focus = focus,
            scoreBoost = 0.00f
        )
    }
}

/* -------------------------------------------------------------
   RESONANZ-LOGIK (4-stellig, Muster-Erkennung)
------------------------------------------------------------- */
@Composable
fun getResonanceInfo4(number: String): ResonanceInfo? {
    if (number.length != 4 || number.any { !it.isDigit() }) return null

    val d = number.toCharArray()

    fun allSame() = d.all { it == d[0] }
    fun isAABB() = (d[0] == d[1]) && (d[2] == d[3]) && (d[0] != d[2])
    fun isABAB() = (d[0] == d[2]) && (d[1] == d[3]) && (d[0] != d[1])
    fun isABBA() = (d[0] == d[3]) && (d[1] == d[2]) && (d[0] != d[1])
    fun isAllDifferent() = d.distinct().size == 4

    val pairPositions = (0..2).filter { i -> d[i] == d[i + 1] }
    val focusResId = when {
        pairPositions.isEmpty() -> R.string.resonance_focus_none
        pairPositions.contains(1) -> R.string.resonance_focus_center
        pairPositions.contains(0) -> R.string.resonance_focus_start
        else -> R.string.resonance_focus_end
    }
    val focus = stringResource(focusResId)

    return when {
        allSame() -> ResonanceInfo(
            id = stringResource(R.string.resonance4_id_mono),
            title = stringResource(R.string.resonance4_title_mono),
            shortMeaning = stringResource(R.string.resonance4_meaning_mono),
            focus = focus,
            scoreBoost = 0.06f
        )

        isAABB() -> ResonanceInfo(
            id = stringResource(R.string.resonance4_id_aabb),
            title = stringResource(R.string.resonance4_title_aabb),
            shortMeaning = stringResource(R.string.resonance4_meaning_aabb),
            focus = focus,
            scoreBoost = 0.05f
        )

        isABAB() -> ResonanceInfo(
            id = stringResource(R.string.resonance4_id_abab),
            title = stringResource(R.string.resonance4_title_abab),
            shortMeaning = stringResource(R.string.resonance4_meaning_abab),
            focus = focus,
            scoreBoost = 0.05f
        )

        isABBA() -> ResonanceInfo(
            id = stringResource(R.string.resonance4_id_abba),
            title = stringResource(R.string.resonance4_title_abba),
            shortMeaning = stringResource(R.string.resonance4_meaning_abba),
            focus = focus,
            scoreBoost = 0.04f
        )

        isAllDifferent() -> ResonanceInfo(
            id = stringResource(R.string.resonance4_id_abcd),
            title = stringResource(R.string.resonance4_title_abcd),
            shortMeaning = stringResource(R.string.resonance4_meaning_abcd),
            focus = focus,
            scoreBoost = 0.03f
        )

        else -> ResonanceInfo(
            id = stringResource(R.string.resonance4_id_none),
            title = stringResource(R.string.resonance4_title_none),
            shortMeaning = stringResource(R.string.resonance4_meaning_none),
            focus = focus,
            scoreBoost = 0.0f
        )
    }
}

@Composable
fun createDetailedMessage(number: String): DetailedMessage {
    val uniqueDigitsInOrder = number.toCharArray().distinct()
    val numerologyData = getNumerologyMeaning(number)
    val quersummeBedeutung = numerologyData.meaning
    val title = stringResource(R.string.message_for_the_moment, number)
    val crossSumResult = calculateReducedCrossSum(number)
    val calculationText = stringResource(
        R.string.your_cross_sum_is,
        crossSumResult.fullCalculationText
    )

    val subtitle = quersummeBedeutung
    val components = uniqueDigitsInOrder.map { digit -> digit to getKeywordForDigit(digit) }
    val digitCounts = number.groupingBy { it }.eachCount()
    val messageBuilder = StringBuilder()

    // --- Frequenzscore ---
    var scoreSum = 0.0
    number.forEach { char -> scoreSum += char.digitToInt() }
    var normalizedScore = (scoreSum / number.length) / 9.0
    if (number.contains("11") || number.contains("22") || number.contains("33")) normalizedScore += 0.15
    if (number.contains("99") || number.contains("88") || number.contains("77")) normalizedScore += 0.1

    // --- Resonanz ---
    val resonance = when (number.length) {
        6 -> getResonanceInfo6(number)
        4 -> getResonanceInfo4(number)
        else -> null
    }

    val resonanceBoost = resonance?.scoreBoost ?: 0f
    val finalFrequency = (normalizedScore + resonanceBoost).coerceIn(0.1, 1.0).toFloat()

    // --- Digit-Intro Texte ---
    uniqueDigitsInOrder.forEach { digit ->
        val count = digitCounts[digit] ?: 1
        val countText = if (count > 1) " " + stringResource(R.string.digit_occurrence, count) else ""

        val introResId = when (digit) {
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

        if (introResId != 0) {
            messageBuilder.append(stringResource(introResId, countText)).append("\n")
        }
    }

    // --- Energiefluss Basis ---
    var energyFlow = components.joinToString(" → ") { it.second }

    // Prefix-Blocks (bleiben immer)
    val angelMessages = getAllAngelMessages(number)
    val angelPrefix = if (angelMessages.isNotEmpty()) {
        angelMessages.joinToString("\n") { "🕊️ $it" } + "\n\n"
    } else {
        ""
    }

    val resonancePrefix = if (resonance != null) {
        "🔮 ${resonance.title}\n${resonance.shortMeaning}\n${resonance.focus}\n\n"
    } else {
        ""
    }

    // --- Summary Core ---
    var summaryCore = numerologyData.karmicDetail ?: stringResource(R.string.summary_default)

    when {
        number.contains("99") && number.contains('1') -> {
            summaryCore = stringResource(R.string.summary_99_1)
            energyFlow = stringResource(R.string.energy_flow_99_1)
        }

        number.contains('9') && number.contains('1') && !number.contains("99") -> {
            summaryCore = stringResource(R.string.summary_1_9)
            energyFlow = stringResource(R.string.energy_flow_1_9)
        }

        number.contains("8") && number.contains("55") -> {
            summaryCore = stringResource(R.string.summary_8_55)
            energyFlow = stringResource(R.string.energy_flow_8_55)
        }

        number.contains('4') && number.contains('8') -> {
            summaryCore = stringResource(R.string.summary_4_8)
            energyFlow = stringResource(R.string.energy_flow_4_8)
        }

        number.contains("11") && number.contains("22") -> {
            summaryCore = stringResource(R.string.summary_11_22)
            energyFlow = stringResource(R.string.energy_flow_11_22)
        }

        number.contains("77") && number.contains('7') -> {
            summaryCore = stringResource(R.string.summary_7_77)
            energyFlow = stringResource(R.string.energy_flow_7_77)
        }

        number.contains("33") && number.contains('3') -> {
            summaryCore = stringResource(R.string.summary_3_33)
            energyFlow = stringResource(R.string.energy_flow_3_33)
        }
    }

    val summary = angelPrefix + resonancePrefix + summaryCore

    return DetailedMessage(
        number = number,
        title = title,
        subtitle = subtitle,
        calculationText = calculationText,
        components = components,
        message = messageBuilder.toString().trim(),
        summary = summary,
        energyFlow = energyFlow,
        frequencyScore = finalFrequency,
        resonanceId = resonance?.id
    )
}

@Composable
fun getNumerologyMeaning(num: String): NumberMeaning {
    val clean = num.filter { it.isDigit() }
    if (clean.isEmpty()) return NumberMeaning(stringResource(id = R.string.numerology_unknown), null)

    val crossSumResult = calculateReducedCrossSum(clean)
    val sumBeforeReduce = clean.sumOf { it.digitToInt() }
    var s = crossSumResult.finalValue
    val specialMeanings = mutableListOf<String>()

    when (sumBeforeReduce) {
        11 -> specialMeanings.add(stringResource(R.string.master_number_11))
        22 -> specialMeanings.add(stringResource(R.string.master_number_22))
        33 -> specialMeanings.add(stringResource(R.string.master_number_33))
        44 -> specialMeanings.add(stringResource(R.string.master_number_44))
        55 -> specialMeanings.add(stringResource(R.string.special_number_55))
        66 -> specialMeanings.add(stringResource(R.string.special_number_66))
        77 -> specialMeanings.add(stringResource(R.string.special_number_77))
        88 -> specialMeanings.add(stringResource(R.string.special_number_88))
        99 -> specialMeanings.add(stringResource(R.string.special_number_99))
    }

    val karmicDetailResId = when (sumBeforeReduce) {
        1 -> R.string.karmic_detail_1
        2 -> R.string.karmic_detail_2
        3 -> R.string.karmic_detail_3
        4 -> R.string.karmic_detail_4
        5 -> R.string.karmic_detail_5
        6 -> R.string.karmic_detail_6
        7 -> R.string.karmic_detail_7
        8 -> R.string.karmic_detail_8
        9 -> R.string.karmic_detail_9
        10 -> R.string.karmic_detail_10.also { specialMeanings.add(stringResource(R.string.karmic_lesson_10)) }
        11 -> R.string.karmic_detail_11.also { specialMeanings.add(stringResource(R.string.karmic_lesson_11)) }
        12 -> R.string.karmic_detail_12
        13 -> R.string.karmic_detail_13.also { specialMeanings.add(stringResource(R.string.karmic_lesson_13)) }
        14 -> R.string.karmic_detail_14.also { specialMeanings.add(stringResource(R.string.karmic_lesson_14)) }
        16 -> R.string.karmic_detail_16.also { specialMeanings.add(stringResource(R.string.karmic_lesson_16)) }
        19 -> R.string.karmic_detail_19.also { specialMeanings.add(stringResource(R.string.karmic_lesson_19)) }
        20 -> R.string.karmic_detail_20
        21 -> R.string.karmic_detail_21
        22 -> R.string.karmic_detail_22.also { specialMeanings.add(stringResource(R.string.karmic_lesson_22)) }
        23 -> R.string.karmic_detail_23
        24 -> R.string.karmic_detail_24
        27 -> R.string.karmic_detail_27
        30 -> R.string.karmic_detail_30
        31 -> R.string.karmic_detail_31
        33 -> R.string.karmic_detail_33.also { specialMeanings.add(stringResource(R.string.karmic_lesson_33)) }
        else -> null
    }

    val karmicDetailText = karmicDetailResId?.let { stringResource(it) }

    if (sumBeforeReduce !in listOf(11, 22, 33)) {
        while (s > 9) s = s.toString().sumOf { it.digitToInt() }
    }

    val baseMeaning = when (s) {
        1 -> stringResource(R.string.numerology_1)
        2 -> stringResource(R.string.numerology_2)
        3 -> stringResource(R.string.numerology_3)
        4 -> stringResource(R.string.numerology_4)
        5 -> stringResource(R.string.numerology_5)
        6 -> stringResource(R.string.numerology_6)
        7 -> stringResource(R.string.numerology_7)
        8 -> stringResource(R.string.numerology_8)
        9 -> stringResource(R.string.numerology_9)
        11 -> stringResource(R.string.numerology_11)
        22 -> stringResource(R.string.numerology_22)
        33 -> stringResource(R.string.numerology_33)
        else -> stringResource(R.string.numerology_unknown)
    }

    val finalString = if (specialMeanings.isNotEmpty()) {
        val distinct = specialMeanings.distinct()
        "$baseMeaning\n" + distinct.joinToString("\n") { "• $it" }
    } else {
        baseMeaning
    }

    return NumberMeaning(finalString, karmicDetailText)
}

/* -------------------------------------------------------------
   UI
------------------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagicNumberApp() {
    val showDebugInfo: kotlin.Boolean = "debug" == Build.TYPE || "eng" == Build.TYPE
    var currentNumber by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    val detailedMessage = if (currentNumber.isNotEmpty()) {
        createDetailedMessage(number = currentNumber)
    } else {
        remember { DetailedMessage() }
    }

    fun triggerGeneration() {
        val seed = System.currentTimeMillis()
        val random = Random(seed)
        currentNumber = (1..6).joinToString("") { random.nextInt(0, 10).toString() }
    }

    fun openPaypal() {
        val paypalUrl = context.getString(R.string.url_paypal_donation)
        context.startActivity(Intent(Intent.ACTION_VIEW, paypalUrl.toUri()))
    }

    fun openGithub() {
        val githubUrl = context.getString(R.string.url_github_repository)
        context.startActivity(Intent(Intent.ACTION_VIEW, githubUrl.toUri()))
    }

    val darkBackgroundColor = Color(0xFF1C1B1F)
    val cardBackgroundColor = Color(0xFF2E2C32)
    val primaryTextColor = Color(0xFFEADDFF)
    val secondaryTextColor = Color(0xFFCAC4D0)
    val accentColor = Color(0xFFD0BCFF)

    Scaffold(
        containerColor = darkBackgroundColor,
        topBar = { /* empty */ },
        bottomBar = {
            BottomAppBar(
                containerColor = darkBackgroundColor.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (detailedMessage.number.isNotEmpty()) {
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipText = detailedMessage.asFormattedString(
                                context.getString(R.string.section_vibration),
                                context.getString(R.string.section_your_message),
                                context.getString(R.string.section_summary),
                                context.getString(R.string.section_energy),
                                context.getString(R.string.frequency_label)
                            )
                            clipboard.setPrimaryClip(
                                ClipData.newPlainText(context.getString(R.string.clipboard_label), clipText)
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = stringResource(R.string.copy_message),
                                tint = secondaryTextColor
                            )
                        }

                        IconButton(onClick = { openPaypal() }) { Text(text = "🤝", fontSize = 24.sp) }
                        IconButton(onClick = { openGithub() }) { Text(text = "ℹ️", fontSize = 24.sp) }
                    } else {
                        Spacer(Modifier.size(48.dp))
                    }

                    Button(
                        onClick = { triggerGeneration() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryTextColor,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(stringResource(R.string.new_message))
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (detailedMessage.number.isNotEmpty()) {
                val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                val cardWidth = if (screenWidth > 600.dp) 0.7f else 1.0f

                Card(
                    modifier = Modifier
                        .fillMaxWidth(cardWidth)
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = detailedMessage.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = primaryTextColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = detailedMessage.calculationText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = secondaryTextColor.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = detailedMessage.subtitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth(cardWidth)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            stringResource(R.string.section_vibration),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryTextColor
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("🌀", style = MaterialTheme.typography.bodyLarge)
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        stringResource(R.string.frequency_label),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = secondaryTextColor
                                    )
                                    val percentText = (detailedMessage.frequencyScore * 100).toInt()
                                    Text(
                                        "$percentText%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = getFrequencyColor(detailedMessage.frequencyScore)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .background(
                                            Color.Black.copy(alpha = 0.3f),
                                            RoundedCornerShape(4.dp)
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(detailedMessage.frequencyScore)
                                            .fillMaxHeight() // ✅ wichtig: NICHT fillMaxSize()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xFFFF5252),
                                                        Color(0xFFFFD740),
                                                        getFrequencyColor(detailedMessage.frequencyScore)
                                                    )
                                                ),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            detailedMessage.components.forEach { (digit, meaning) ->
                                Text(
                                    buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = accentColor
                                            )
                                        ) { append("$digit – ") }
                                        withStyle(style = SpanStyle(color = secondaryTextColor)) { append(meaning) }
                                    }
                                )
                            }
                        }

                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))

                        Text(
                            text = stringResource(R.string.section_your_message),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryTextColor
                        )

                        Text(
                            text = markdownToAnnotatedString(detailedMessage.message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = secondaryTextColor,
                            lineHeight = 24.sp
                        )

                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))

                        Text(
                            stringResource(R.string.section_summary),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = primaryTextColor
                        )

                        // Debug-only: Resonanz-ID anzeigen
                        if (showDebugInfo && detailedMessage.resonanceId != null) {
                            Text(
                                text = detailedMessage.resonanceId,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Text(
                            text = detailedMessage.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = secondaryTextColor
                        )

                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))

                        Text(
                            stringResource(R.string.section_energy),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryTextColor
                        )
                        Text(
                            text = detailedMessage.energyFlow,
                            style = MaterialTheme.typography.bodyMedium,
                            color = accentColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.welcome_message),
                        color = secondaryTextColor,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}