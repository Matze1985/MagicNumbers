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
    // CLEAN MARKDOWN
    // --------------------------------------------------
    fun String.cleanMarkdown(): String {
        return this.replace(Regex("\\*+"), "")
    }
}