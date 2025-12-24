package com.spiritual.magicnumbers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
    val frequencyScore: Float = 0f
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
            |${message.replace("*", "")}}
            |
            |$summaryHeader
            |$summary
            |
            |$energyHeader
            |$energyFlow
        """.trimMargin()
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

// --- MARKDOWN-FUNKTIONEN ---
@Composable
fun markdownToAnnotatedString(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val parts = text.split("**")
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(part)
                }
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

@Composable
fun createDetailedMessage(number: String): DetailedMessage {
    val uniqueDigitsInOrder = number.toCharArray().distinct()
    val numerologyData = getNumerologyMeaning(number)
    val quersummeBedeutung = numerologyData.meaning
    val title = stringResource(R.string.message_for_the_moment, number)
    val calculationSum = number.map { it.digitToInt() }.sum()
    val calculationRaw = number.toCharArray().joinToString(" + ") + " = $calculationSum"
    val calculationText = stringResource(R.string.your_cross_sum_is, calculationRaw)
    val subtitle = quersummeBedeutung
    val components = uniqueDigitsInOrder.map { digit -> digit to getKeywordForDigit(digit) }
    val digitCounts = number.groupingBy { it }.eachCount()
    val messageBuilder = StringBuilder()
    var scoreSum = 0.0
    number.forEach { char -> scoreSum += char.digitToInt() }
    var normalizedScore = (scoreSum / number.length) / 9.0
    if (number.contains("11") || number.contains("22") || number.contains("33")) normalizedScore += 0.15
    if (number.contains("99") || number.contains("88") || number.contains("77")) normalizedScore += 0.1
    val finalFrequency = normalizedScore.coerceIn(0.1, 1.0).toFloat()

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

    var summary = numerologyData.karmicDetail ?: stringResource(R.string.summary_default)
    var energyFlow = components.joinToString(" → ") { it.second }

    // Spezielle Überschreibungen für bestimmte Kombinationen
    when {
        // 1. Vollendung & Neubeginn
        number.contains("99") && number.contains('1') -> {
            summary = stringResource(R.string.summary_99_1)
            energyFlow = stringResource(R.string.energy_flow_99_1)
        }
        // 2. Anfang & Ende (Klassiker)
        number.contains('9') && number.contains('1') && !number.contains("99") -> { // Nur wenn keine 99 dabei ist, sonst greift Regel 1
            summary = stringResource(R.string.summary_1_9)
            energyFlow = stringResource(R.string.energy_flow_1_9)
        }
        // 3. Materielle Macht & Wandel
        number.contains("8") && number.contains("55") -> {
            summary = stringResource(R.string.summary_8_55)
            energyFlow = stringResource(R.string.energy_flow_8_55)
        }
        // 4. Struktur & Erfolg (Business-Zahl)
        number.contains('4') && number.contains('8') -> {
            summary = stringResource(R.string.summary_4_8)
            energyFlow = stringResource(R.string.energy_flow_4_8)
        }
        // 5. Meister-Allianz (Vision & Bauen)
        number.contains("11") && number.contains("22") -> {
            summary = stringResource(R.string.summary_11_22)
            energyFlow = stringResource(R.string.energy_flow_11_22)
        }
        // 6. Mystische Tiefe (7 & 77)
        number.contains("77") && number.contains('7') -> {
            summary = stringResource(R.string.summary_7_77)
            energyFlow = stringResource(R.string.energy_flow_7_77)
        }
        // 7. Heilender Ausdruck (3 & 33)
        number.contains("33") && number.contains('3') -> {
            summary = stringResource(R.string.summary_3_33)
            energyFlow = stringResource(R.string.energy_flow_3_33)
        }
    }

    return DetailedMessage(number, title, subtitle, calculationText, components, messageBuilder.toString().trim(), summary, energyFlow, finalFrequency)
}

@Composable
fun getNumerologyMeaning(num: String): NumberMeaning {
    val clean = num.filter { it.isDigit() }
    if (clean.isEmpty()) return NumberMeaning(stringResource(id = R.string.numerology_unknown), null)

    val sumBeforeReduce = clean.sumOf { it.digitToInt() }
    var s = sumBeforeReduce
    val specialMeanings = mutableListOf<String>()

    // --- 1. PRÜFUNG AUF MEISTERZAHLEN & SPEZIALZAHLEN (Vor der Reduktion) ---
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

    // --- 2. ZUWEISUNG KARMISCHER DETAILS & LEKTIONEN ---
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
        33 -> R.string.karmic_detail_33.also { specialMeanings.add(stringResource(R.string.karmic_lesson_33))}
        else -> null
    }

    val karmicDetailText = karmicDetailResId?.let { stringResource(it) }

    // --- 3. REDUKTION AUF EINE ZIFFER ---
    // Wir reduzieren NICHT, wenn es eine der klassischen Meisterzahlen (11, 22, 33) ist.
    // Andere hohe Zahlen wie 44, 55 etc. werden für die Grundbedeutung trotzdem auf ihre Basis reduziert,
    // behalten aber ihre "Special Meaning"-Zusatzinfo.
    if (sumBeforeReduce !in listOf(11, 22, 33)) {
        while (s > 9) {
            s = s.toString().sumOf { it.digitToInt() }
        }
    }

    val baseMeaning = when (s) {
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

    // Kombiniere Grundbedeutung mit Spezialbedeutungen
    val finalString = if (specialMeanings.isNotEmpty()) {
        // Entfernt Duplikate, falls eine Bedeutung durch mehrere Logik-Blöcke hinzugefügt wurde
        val distinctSpecialMeanings = specialMeanings.distinct()
        "$baseMeaning\n" + distinctSpecialMeanings.joinToString("\n") { "• $it" }
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
        currentNumber = random.nextInt(100_000, 1_000_000).toString()
    }

    // PayPal Spendenlink-Funktion
    fun openPaypal() {
        val paypalUrl = "https://paypal.me/MathiasN" // Dein PayPal.me Link
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paypalUrl))
        context.startActivity(intent)
    }

    val darkBackgroundColor = Color(0xFF1C1B1F)
    val cardBackgroundColor = Color(0xFF2E2C32)
    val primaryTextColor = Color(0xFFEADDFF)
    val secondaryTextColor = Color(0xFFCAC4D0)
    val accentColor = Color(0xFFD0BCFF)

    Scaffold(
        containerColor = darkBackgroundColor,
        topBar = { /* TopBar ist leer, wie gewünscht */ },
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
                        // 1. Der Copy-Button
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                context.getString(R.string.clipboard_label),
                                detailedMessage.asFormattedString(
                                    context.getString(R.string.section_vibration),
                                    context.getString(R.string.section_your_message),
                                    context.getString(R.string.section_summary),
                                    context.getString(R.string.section_energy),
                                    context.getString(R.string.frequency_label)
                                )
                            )
                            clipboard.setPrimaryClip(clip)
                        }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = stringResource(R.string.copy_message),
                                tint = secondaryTextColor
                            )
                        }

                        // 2. Der Donate Button mit Handshake
                        IconButton(onClick = { openPaypal() }) {
                            Text(
                                text = "🤝",
                                fontSize = 24.sp
                            )
                        }

                    } else {
                        // Platzhalter, wenn keine Nachricht da ist
                        Spacer(Modifier.size(48.dp))
                    }

                    // 3. Neue Nachricht Button (Rechteckig, wie gewünscht)
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

                // KARTE 1: TITEL, RECHENWEG & BEDEUTUNG
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
                        // 1. Titel ("Botschaft für den Moment: ...")
                        Text(
                            text = detailedMessage.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = primaryTextColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        // 2. Rechenweg ("Deine Quersumme lautet: 1 + ... = X")
                        Text(
                            text = detailedMessage.calculationText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = secondaryTextColor.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 3. Bedeutung ("Numerologie Spezialbedeutungen")
                        Text(
                            text = detailedMessage.subtitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // KARTE 2: DETAILS MIT FREQUENZ
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
                        // Sektion: Schwingung
                        Text(stringResource(R.string.section_vibration), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = primaryTextColor)
                        // --- FREQUENZ ZEILE (Kompakt) ---
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
                                    Text(stringResource(R.string.frequency_label), style = MaterialTheme.typography.bodySmall, color = secondaryTextColor)
                                    val percentText = (detailedMessage.frequencyScore * 100).toInt()
                                    Text("$percentText%", style = MaterialTheme.typography.bodySmall, color = getFrequencyColor(detailedMessage.frequencyScore))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(detailedMessage.frequencyScore)
                                            .fillMaxHeight()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xFFFF5252), // Rot
                                                        Color(0xFFFFD740), // Gelb
                                                        getFrequencyColor(detailedMessage.frequencyScore) // Ziel-Farbe
                                                    )
                                                ),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                    )
                                }
                            }
                        }

                        // Ziffern Auflistung
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            detailedMessage.components.forEach { (digit, meaning) ->
                                Text(buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = accentColor)) { append("$digit – ") }
                                    withStyle(style = SpanStyle(color = secondaryTextColor)) { append(meaning) }
                                })
                            }
                        }

                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                        // Sektion: Deine Botschaft
                        Text(
                            text = stringResource(R.string.section_your_message),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryTextColor
                        )

                        val formattedMessage = markdownToAnnotatedString(
                            detailedMessage.message
                        )

                        Text(
                            text = formattedMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = secondaryTextColor,
                            lineHeight = 24.sp
                        )

                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                        // Sektion: Kurze Zusammenfassung
                        Text(stringResource(R.string.section_summary), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = primaryTextColor)
                        Text(text = detailedMessage.summary, style = MaterialTheme.typography.bodyMedium, color = secondaryTextColor)
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                        // Sektion: Energie
                        Text(stringResource(R.string.section_energy), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = primaryTextColor)
                        Text(text = detailedMessage.energyFlow, style = MaterialTheme.typography.bodyMedium, color = accentColor, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                // WILLKOMMENS-NACHRICHT
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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