package com.spiritual.magicnumbers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.core.net.toUri
import kotlin.random.Random

object Utils {
    // --------------------------------------------------
    // RANDOM NUMBER GENERATOR
    // --------------------------------------------------
    fun generateNumber(length: Int = 6): String {
        val random = Random(System.currentTimeMillis())
        return (1..length).joinToString("") {
            random.nextInt(0, 10).toString()
        }
    }

    // --------------------------------------------------
    // OPEN PAYPAL
    // --------------------------------------------------
    fun openPaypal(context: Context) {
        val url = context.getString(R.string.url_paypal_donation)
        context.startActivity(
            Intent(Intent.ACTION_VIEW, url.toUri())
        )
    }

    // --------------------------------------------------
    // OPEN GITHUB
    // --------------------------------------------------
    fun openGithub(context: Context) {
        val url = context.getString(R.string.url_github_repository)
        context.startActivity(
            Intent(Intent.ACTION_VIEW, url.toUri())
        )
    }

    // --------------------------------------------------
    // COPY TO CLIPBOARD
    // --------------------------------------------------
    fun copyToClipboard(context: Context, text: String, showToast: Boolean = true) {
        if (text.isBlank()) return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(context.getString(R.string.app_name), text)
        clipboard.setPrimaryClip(clip)
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

    // --------------------------------------------------
    // MARKDOWN TO ANNOTATED STRING
    // --------------------------------------------------
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

    // --------------------------------------------------
    // GET SPECIAL MEANING
    // --------------------------------------------------
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

    // --------------------------------------------------
    // CLEAN MARKDOWN
    // --------------------------------------------------
    fun String.cleanMarkdown(): String {
        return this.replace(Regex("\\*+"), "")
    }
}