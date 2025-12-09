package com.spiritual.magicnumbers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// --- DATENKLASSEN ---
data class NumberMeaning(val meaning: String)
data class DetailedMessage(
    val number: String = "",
    val title: String = "",
    val subtitle: String = "",
    val components: List<Pair<Char, String>> = emptyList(),
    val message: String = "",
    val summary: String = "",
    val energyFlow: String = ""
) {
    fun asFormattedString(
        vibrationHeader: String, messageHeader: String, summaryHeader: String, energyHeader: String
    ): String {
        val componentString = components.joinToString("\n") { (digit, meaning) -> "$digit – $meaning" }
        return """
            $title
            $subtitle

            $vibrationHeader
            $componentString

            $messageHeader
            ${message.replace("**", "").replace("✨ ", "\n- ")}

            $summaryHeader
            $summary

            $energyHeader
            $energyFlow
        """.trimIndent()
    }
}

// --- MAIN ACTIVITY ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.hashCode()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.hashCode())
        )
        super.onCreate(savedInstanceState)
        setContent {
            MagicNumberApp()
        }
    }
}

// --- LOGIK-FUNKTIONEN ---
@Composable
fun createDetailedMessage(number: String): DetailedMessage {
    val uniqueDigitsInOrder = number.toCharArray().distinct()
    val quersummeBedeutung = getNumerologyMeaning(number).meaning
    val title = stringResource(R.string.message_for_the_moment, number)
    val subtitle = stringResource(R.string.your_cross_sum_is, quersummeBedeutung)
    val components = uniqueDigitsInOrder.map { digit -> digit to getKeywordForDigit(digit) }
    val digitCounts = number.groupingBy { it }.eachCount()
    val messageBuilder = StringBuilder()
    uniqueDigitsInOrder.forEach { digit ->
        val count = digitCounts[digit] ?: 1
        val countText = if (count > 1) stringResource(R.string.digit_occurrence, count) else ""
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
    var summary = stringResource(R.string.summary_default)
    var energyFlow = components.map { it.second }.joinToString(" → ")
    when {
        number.contains("99") && number.contains('1') -> {
            summary = stringResource(R.string.summary_99_1)
            energyFlow = stringResource(R.string.energy_flow_99_1)
        }
        number.contains("8") && number.contains("55") -> {
            summary = stringResource(R.string.summary_8_55)
            energyFlow = stringResource(R.string.energy_flow_8_55)
        }
    }
    return DetailedMessage(number, title, subtitle, components, messageBuilder.toString().trim(), summary, energyFlow)
}

@Composable
fun getNumerologyMeaning(num: String): NumberMeaning {
    val clean = num.filter { it.isDigit() }
    if (clean.isEmpty()) return NumberMeaning(stringResource(id = R.string.numerology_unknown))
    val sumBeforeReduce = clean.sumOf { it.digitToInt() }
    var s = sumBeforeReduce
    val specialMeaning = when (sumBeforeReduce) {
        11 -> " " + stringResource(R.string.master_number_11)
        22 -> " " + stringResource(R.string.master_number_22)
        33 -> " " + stringResource(R.string.master_number_33)
        13 -> " " + stringResource(R.string.karmic_lesson_13)
        14 -> " " + stringResource(R.string.karmic_lesson_14)
        16 -> " " + stringResource(R.string.karmic_lesson_16)
        19 -> " " + stringResource(R.string.karmic_lesson_19)
        else -> ""
    }
    if (sumBeforeReduce !in listOf(11, 22, 33)) {
        while (s > 9) {
            s = s.toString().sumOf { it.digitToInt() }
        }
    }
    val meaning = when (s) {
        1 -> stringResource(id = R.string.numerology_1)
        2 -> stringResource(id = R.string.numerology_2)
        3 -> stringResource(id = R.string.numerology_3)
        4 -> stringResource(id = R.string.numerology_4)
        5 -> stringResource(id = R.string.numerology_5)
        6 -> stringResource(id = R.string.numerology_6)
        7 -> stringResource(id = R.string.numerology_7)
        8 -> stringResource(id = R.string.numerology_8)
        9 -> stringResource(id = R.string.numerology_9)
        11 -> stringResource(id = R.string.numerology_11)
        22 -> stringResource(id = R.string.numerology_22)
        33 -> stringResource(id = R.string.numerology_33)
        else -> stringResource(id = R.string.numerology_unknown)
    }
    return NumberMeaning(meaning + specialMeaning)
}

@Composable
fun getKeywordForDigit(digit: Char): String {
    return when (digit) {
        '0' -> stringResource(id = R.string.keyword_0)
        '1' -> stringResource(id = R.string.keyword_1)
        '2' -> stringResource(id = R.string.keyword_2)
        '3' -> stringResource(id = R.string.keyword_3)
        '4' -> stringResource(id = R.string.keyword_4)
        '5' -> stringResource(id = R.string.keyword_5)
        '6' -> stringResource(id = R.string.keyword_6)
        '7' -> stringResource(id = R.string.keyword_7)
        '8' -> stringResource(id = R.string.keyword_8)
        '9' -> stringResource(id = R.string.keyword_9)
        else -> stringResource(id = R.string.numerology_unknown)
    }
}

/* -------------------------------------------------------------
   UI (FINAL MIT NEUER STRUKTUR)
------------------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagicNumberApp() {
    var currentNumber by remember { mutableStateOf("") }
    val detailedMessage = if (currentNumber.isNotEmpty()) {
        createDetailedMessage(number = currentNumber)
    } else {
        remember { DetailedMessage() }
    }
    fun triggerGeneration() {
        val seed = System.currentTimeMillis()
        val random = Random(seed) // Initialisiere den Zufallsgenerator mit diesem Samen.
        // Generiere eine Zahl im gleichen Bereich wie zuvor, aber basierend auf dem Zeit-Samen.
        currentNumber = random.nextInt(100_000, 1_000_000).toString()
    }


    val darkBackgroundColor = Color(0xFF1C1B1F)
    val cardBackgroundColor = Color(0xFF2E2C32)
    val primaryTextColor = Color(0xFFEADDFF)
    val secondaryTextColor = Color(0xFFCAC4D0)
    val accentColor = Color(0xFFD0BCFF)

    Scaffold(
        containerColor = darkBackgroundColor,
        topBar = { /* TopBar ist jetzt leer, da die Titel in einer eigenen Karte sind */ },
        bottomBar = {
            BottomAppBar(containerColor = darkBackgroundColor.copy(alpha = 0.95f), tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Richtiger Code
                    if (detailedMessage.number.isNotEmpty()) {
                        val context = LocalContext.current

                        // 1. Lade alle benötigten Strings VORHER im @Composable-Kontext.
                        val vibrationHeader = stringResource(R.string.section_vibration)
                        val messageHeader = stringResource(R.string.section_your_message)
                        val summaryHeader = stringResource(R.string.section_summary)
                        val energyHeader = stringResource(R.string.section_energy)
                        val clipboardLabel = stringResource(R.string.clipboard_label)
                        val copyMessageDescription = stringResource(R.string.copy_message)

                        IconButton(onClick = {
                            // 2. Verwende jetzt die normalen String-Variablen im onClick-Block.
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                clipboardLabel,
                                detailedMessage.asFormattedString(
                                    vibrationHeader,
                                    messageHeader,
                                    summaryHeader,
                                    energyHeader
                                )
                            )
                            clipboard.setPrimaryClip(clip)
                        }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = copyMessageDescription, // Auch hier die Variable verwenden
                                tint = secondaryTextColor
                            )
                        }
                    } else {
                        Spacer(Modifier.size(48.dp))
                    }
                    Button(
                        onClick = { triggerGeneration() },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryTextColor, contentColor = Color.Black)
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

                // *** NEU: Eigene Karte für Titel und Quersumme ***
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
                            color = primaryTextColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = detailedMessage.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                            color = secondaryTextColor
                        )
                    }
                }

                // *** Bestehende Karte für den Detail-Inhalt ***
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
                        // Sektion: Schwingung der Ziffern
                        Text(stringResource(R.string.section_vibration), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = primaryTextColor)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            detailedMessage.components.forEach { (digit, meaning) ->
                                Text(buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = accentColor)) { append("$digit – ") }
                                    withStyle(style = SpanStyle(color = secondaryTextColor)) { append(meaning) }
                                })
                            }
                        }
                        Divider(color = secondaryTextColor.copy(alpha = 0.3f))

                        // Sektion: Deine Botschaft
                        Text(stringResource(R.string.section_your_message), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = primaryTextColor)
                        Text(buildAnnotatedString {
                            val message = detailedMessage.message
                            var start = 0
                            while (start < message.length) {
                                val boldStart = message.indexOf("**", start).takeIf { it != -1 } ?: message.length
                                val pointStart = message.indexOf("✨", start).takeIf { it != -1 } ?: message.length
                                val nextSpecial = minOf(boldStart, pointStart)
                                withStyle(style = SpanStyle(color = secondaryTextColor)) { append(message.substring(start, nextSpecial)) }
                                if (nextSpecial == boldStart) {
                                    val boldEnd = message.indexOf("**", boldStart + 2)
                                    if (boldEnd != -1) {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) { append(message.substring(boldStart + 2, boldEnd)) }
                                        start = boldEnd + 2
                                    } else { start = message.length }
                                } else if (nextSpecial == pointStart) {
                                    append("✨ ")
                                    start = pointStart + 1
                                } else {
                                    start = message.length
                                }
                            }
                        }, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                        Divider(color = secondaryTextColor.copy(alpha = 0.3f))

                        // Sektion: Zusammenfassung
                        Text(stringResource(R.string.section_summary), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = primaryTextColor)
                        Text(detailedMessage.summary, style = MaterialTheme.typography.bodyLarge, color = secondaryTextColor)
                        Divider(color = secondaryTextColor.copy(alpha = 0.3f))

                        // Sektion: Energiefluss
                        Text(stringResource(R.string.section_energy), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = primaryTextColor)
                        Text(detailedMessage.energyFlow, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = accentColor)
                    }
                }

            } else {
                // Startansicht
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.welcome_message),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp),
                        color = secondaryTextColor
                    )
                }
            }
        }
    }
}