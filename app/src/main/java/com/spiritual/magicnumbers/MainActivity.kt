package com.spiritual.magicnumbers

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.spiritual.magicnumbers.NumerologyEngine.buildVisibleCopyText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.parseColor("#2A0050")
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightNavigationBars = false // weiße Icons
        setContent { MagicNumbersApp() }
    }

    @SuppressLint("UnusedContentLambdaTargetStateParameter")
    @Composable
    fun MagicNumbersApp() {

        val context = LocalContext.current
        var currentNumber by rememberSaveable { mutableStateOf("") }

        val engineResult = remember(currentNumber) {
            if (currentNumber.isNotEmpty())
                NumerologyEngine.buildEngineResult(currentNumber)
            else null
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2A0050),
                            Color(0xFF120020),
                            Color(0xFF000000)
                        )
                    )
                )
        ) {

            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {

                    BottomAppBar(
                        containerColor = Color(0xFF1A1A2E)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Row {

                                IconButton(onClick = { Utils.openPaypal(context) }) {
                                    Text("🤝", fontSize = 22.sp)
                                }

                                IconButton(onClick = { Utils.openGithub(context) }) {
                                    Text("ℹ️", fontSize = 22.sp)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Absolute.Right
                            ) {

                                if (currentNumber.isNotEmpty()) {
                                    Button(
                                        onClick = {
                                            val copyText = engineResult?.buildVisibleCopyText(context)
                                            Utils.copyToClipboard(context, copyText.toString())
                                        }
                                    ) {
                                        Icon (
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }

                                Button(
                                    onClick = { currentNumber = Utils.generateNumber() },
                                ) {
                                    Text(
                                        text = stringResource(R.string.new_message)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        null
                                    )
                                }
                            }
                        }
                    }
                }
            ) { padding ->

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (engineResult == null) {

                        Spacer(Modifier.height(100.dp))

                        Text(
                            stringResource(R.string.welcome_message),
                            color = Color.White,
                            fontSize = 18.sp
                        )

                    } else {

                        AnimatedContent(
                            targetState = currentNumber,
                            transitionSpec = {
                                fadeIn(tween(900)) togetherWith fadeOut(tween(500))
                            },
                            label = ""
                        ) {

                            val percent = (engineResult.frequencyScore * 100).toInt()

                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1E1E2E)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            ) {

                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {

                                    // Titel
                                    Text(
                                        stringResource(engineResult.numerologyTitleResId),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB388FF)
                                    )

                                    // Botschaft für den Moment
                                    Text(
                                        stringResource(
                                            R.string.section_message_for_the_moment,
                                            currentNumber
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    // Quersumme
                                    Text(
                                        stringResource(R.string.your_cross_sum_is),
                                        color = Color.LightGray
                                    )

                                    Text(
                                        engineResult.crossSum.fullCalculationText,
                                        color = Color.LightGray
                                    )

                                    // Masters von getOrderedMasters
                                    val masters = NumerologyEngine.getOrderedMasters(
                                        number = engineResult.number,
                                        crossReduced = engineResult.crossSum.reducedSum,
                                        masterCore = engineResult.masterCore,
                                        masterAmplifiers = engineResult.masterAmplifiers)

                                    if (masters.isNotEmpty()) {

                                        Text(
                                            text = stringResource(R.string.section_master_energy),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )

                                        masters.forEach { master ->

                                            NumerologyEngine.getSpecialMeaningResId(master)
                                                ?.let { resId ->

                                                    Text(
                                                        text = stringResource(resId),
                                                        color = Color.LightGray
                                                    )
                                                }
                                        }
                                    }

                                    // Engelszahlen
                                    if (engineResult.angelImpulseResId != 0 || engineResult.angelResIds.isNotEmpty()) {

                                        Text(
                                            text = stringResource(R.string.section_angel),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )

                                        val shownAngelIds = mutableSetOf<Int>()

                                        if (engineResult.angelImpulseResId != 0) {

                                            shownAngelIds.add(engineResult.angelImpulseResId)

                                            Text(
                                                text = stringResource(engineResult.angelImpulseResId),
                                                color = Color.LightGray
                                            )
                                        }

                                        engineResult.angelResIds
                                            .distinct()
                                            .forEach { resId ->

                                                if (!shownAngelIds.contains(resId)) {

                                                    shownAngelIds.add(resId)

                                                    Text(
                                                        text = stringResource(resId),
                                                        color = Color.LightGray
                                                    )
                                                }
                                            }
                                    }

                                    Divider(color = Color.DarkGray)

                                    // Frequenz
                                    Text(
                                        text="${stringResource(R.string.section_frequency)} $percent%",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    val dynamicColor = Utils.frequencyColor(engineResult.frequencyScore)

                                    LinearProgressIndicator(
                                        progress = engineResult.frequencyScore,
                                        color = dynamicColor,
                                        trackColor = Color.DarkGray
                                    )

                                    Divider(color = Color.DarkGray)

                                    // Schwingung der Ziffern
                                    Text(
                                        stringResource(R.string.section_vibration),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    val uniqueDigits = engineResult.number.filter { it.isDigit() }.toList().distinct()
                                    uniqueDigits.forEach { digit ->
                                        val keywordRes = when (digit) {
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

                                        keywordRes?.let {
                                            Text(
                                                text = "$digit – ${stringResource(it)}",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFD6C2FF),
                                            )
                                        }
                                    }

                                    Divider(color = Color.DarkGray)

                                    // Deine Botschaft
                                    Text(
                                        stringResource(R.string.section_description_numbers),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    val counts = engineResult.number.groupingBy { it }.eachCount()

                                    counts.forEach { (digit, count) ->
                                        val introRes =
                                            engineResult.digitIntroResIds.find { it.first == digit }?.second
                                        val amplifyText = if (count >= 2) stringResource(
                                            R.string.digit_occurrence,
                                            count
                                        ) else ""

                                        introRes?.let {

                                            Text(
                                                text = Utils.markdownToAnnotatedString(
                                                    stringResource(it, " ${amplifyText}")
                                                ),
                                                color = Color.LightGray
                                            )
                                        }
                                    }

                                    Divider(color = Color.DarkGray)

                                    // Resonanzmuster
                                    engineResult.resonanceTitleResId?.let { Text(stringResource(it), fontWeight = FontWeight.Bold, color = Color.White) }

                                    engineResult.resonanceMeaningResId?.let { Text(stringResource(it), color = Color.LightGray) }

                                    engineResult.resonanceFocusResId?.let { Text(stringResource(it), color = Color.LightGray) }

                                    Divider(color = Color.DarkGray)

                                    // Zusammenfassung + Karmische Details
                                    Text(
                                        stringResource(R.string.section_summary),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Text(
                                        stringResource(engineResult.summaryResId),
                                        color = Color.LightGray
                                    )

                                    engineResult.karmicDetailResId?.let { karmicRes ->

                                        Text(
                                            text = stringResource(karmicRes),
                                            color = Color.LightGray
                                        )
                                    }

                                    Divider(color = Color.DarkGray)

                                    // Energiefluss
                                    Text(
                                        stringResource(R.string.section_energy),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Text(
                                        stringResource(engineResult.energyFlowResId),
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}