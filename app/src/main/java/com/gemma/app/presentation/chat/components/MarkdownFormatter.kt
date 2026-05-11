package com.gemma.app.presentation.chat.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

/**
 * A lightweight custom markdown parser that handles simple bolding, headings, and bullet points.
 * For advanced usage, consider adding a library like Markwon or compose-markdown.
 */
fun parseMarkdownToAnnotatedString(text: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")
        
        for ((index, line) in lines.withIndex()) {
            var currentLine = line.trimStart()
            
            val isBullet = currentLine.startsWith("* ") || currentLine.startsWith("- ")
            if (isBullet) {
                currentLine = currentLine.substring(2).trimStart()
            }
            
            val isHeading = currentLine.startsWith("#")
            if (isHeading) {
                currentLine = currentLine.trimStart('#').trimStart()
            }
            
            if (isBullet) {
                append("•  ")
            }
            
            if (isHeading) {
                withStyle(style = SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)) {
                    appendFormattedLine(currentLine)
                }
            } else {
                appendFormattedLine(currentLine)
            }
            
            if (index < lines.size - 1) {
                append("\n")
            }
        }
    }
}

/**
 * Parses **bold** markers inside a single line.
 */
private fun AnnotatedString.Builder.appendFormattedLine(line: String) {
    val parts = line.split("**")
    for ((index, part) in parts.withIndex()) {
        val cleanPart = part.replace("*", "")
        if (index % 2 == 1) {
            // It's bold
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                append(cleanPart)
            }
        } else {
            // It's normal text
            withStyle(style = SpanStyle(color = Color.Black)) {
                append(cleanPart)
            }
        }
    }
}
