package com.example.dietplanner.com.example.dietplanner.util

import android.util.Log
import java.util.Locale

object TextCleanupUtil {

    private const val TAG = "TextCleanupUtil"

    /**
     * Cleans diet plan text by removing markdown formatting and normalizing whitespace
     */
    fun cleanDietPlanText(rawText: String): String {
        Log.d(TAG, "Cleaning text of length: ${rawText.length}")

        var cleaned = rawText

        // Remove markdown bold (**text** or __text__)
        cleaned = cleaned.replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
        cleaned = cleaned.replace(Regex("__(.+?)__"), "$1")

        // Remove markdown italic (*text* or _text_)
        cleaned = cleaned.replace(Regex("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)"), "$1")
        cleaned = cleaned.replace(Regex("(?<!_)_(?!_)(.+?)(?<!_)_(?!_)"), "$1")

        // Remove markdown headers (## or ###)
        cleaned = cleaned.replace(Regex("^#{1,6}\\s+", RegexOption.MULTILINE), "")

        // Remove list markers at the start of lines (*, -, +, or numbered lists)
        cleaned = cleaned.replace(Regex("^\\s*[*\\-+]\\s+", RegexOption.MULTILINE), "• ")
        cleaned = cleaned.replace(Regex("^\\s*\\d+\\.\\s+", RegexOption.MULTILINE), "")

        // Remove horizontal rules (---, ***, ___)
        cleaned = cleaned.replace(Regex("^\\s*[-*_]{3,}\\s*$", RegexOption.MULTILINE), "")

        // Remove code blocks (```text```)
        cleaned = cleaned.replace(Regex("```[\\s\\S]*?```"), "")
        cleaned = cleaned.replace(Regex("`(.+?)`"), "$1")

        // Normalize whitespace
        cleaned = cleaned.replace(Regex("\\s+"), " ") // Multiple spaces to single
        cleaned = cleaned.replace(Regex(" +\n"), "\n") // Trailing spaces before newline
        cleaned = cleaned.replace(Regex("\n +"), "\n") // Leading spaces after newline
        cleaned = cleaned.replace(Regex("\n{3,}"), "\n\n") // Multiple newlines to double

        // Trim each line
        cleaned = cleaned.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString("\n")

        // Final trim
        cleaned = cleaned.trim()

        Log.d(TAG, "Cleaned text length: ${cleaned.length}")
        return cleaned
    }

    /**
     * Extracts day-wise sections from diet plan text
     */
    fun extractDayPlans(cleanedText: String): Map<String, String> {
        Log.d(TAG, "Extracting day plans from text")

        val dayPattern = Regex(
            "(?:^|\\n)\\s*(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday|Day1)\\s*:?\\s*\\n",
            RegexOption.IGNORE_CASE
        )

        val matches = dayPattern.findAll(cleanedText).toList()
        val dayPlans = mutableMapOf<String, String>()

        if (matches.isEmpty()) {
            Log.w(TAG, "No day patterns found in text")
            return dayPlans
        }

        for (i in matches.indices) {
            val dayName = matches[i].groupValues[1].replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
            val startIndex = matches[i].range.last + 1
            val endIndex = if (i + 1 < matches.size) {
                matches[i + 1].range.first
            } else {
                cleanedText.length
            }

            val dayContent = cleanedText.substring(startIndex, endIndex).trim()
            dayPlans[dayName] = dayContent
            Log.d(TAG, "Extracted $dayName: ${dayContent.length} chars")
        }

        return dayPlans
    }

    /**
     * Parses a day's content into structured meals and activities
     */
    fun parseDayContent(dayContent: String): DayPlan {
        val breakfast = extractSection(dayContent, "Breakfast", "Lunch")
        val lunch = extractSection(dayContent, "Lunch", "Dinner")
        val dinner = extractSection(dayContent, "Dinner", "Snack")
        val snacks = extractSection(dayContent, "Snack", "Exercise")
        val exercise = extractSection(dayContent, "Exercise", "Hydration")
        val hydration = extractSection(dayContent, "Hydration", null)

        return DayPlan(breakfast, lunch, dinner, snacks, exercise, hydration)
    }

    private fun extractSection(text: String, sectionName: String, nextSection: String?): String {
        val pattern = if (nextSection != null) {
            Regex("$sectionName\\s*:?\\s*([\\s\\S]*?)(?=$nextSection|$)", RegexOption.IGNORE_CASE)
        } else {
            Regex("$sectionName\\s*:?\\s*([\\s\\S]*?)$", RegexOption.IGNORE_CASE)
        }

        return pattern.find(text)?.groupValues?.get(1)?.trim() ?: ""
    }

    data class DayPlan(
        val breakfast: String,
        val lunch: String,
        val dinner: String,
        val snacks: String,
        val exercise: String,
        val hydration: String
    )
}