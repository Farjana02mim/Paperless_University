package com.example.domain.ai.prompt

import com.example.domain.model.ai.AiCategory

/**
 * Reusable Prompt Engineering Pipeline & Security Engine.
 * Separates System Prompt, Developer Prompt, User Prompt, Context Builder, and Templates.
 */

object DataMasker {
    private val CREDIT_CARD_REGEX = Regex("\\b(?:\\d[ -]*?){13,16}\\b")
    private val PHONE_REGEX = Regex("\\b\\+?[0-9]{10,12}\\b")
    private val EMAIL_REGEX = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    private val PASSWORD_KEYWORD_REGEX = Regex("(?i)(password|passwd|pin|secret|token)=\\S+")

    fun maskConfidentialData(input: String): String {
        var masked = input
        masked = CREDIT_CARD_REGEX.replace(masked, "[CARD_MASKED]")
        masked = PHONE_REGEX.replace(masked, "[PHONE_MASKED]")
        masked = EMAIL_REGEX.replace(masked, "[EMAIL_MASKED]")
        masked = PASSWORD_KEYWORD_REGEX.replace(masked, "$1=[REDACTED]")
        return masked
    }
}

class PromptBuilder {
    private var systemPrompt: String = "You are the official Smart Campus AI Assistant for Paperless University."
    private var developerPrompt: String = "Ensure accurate academic responses. Format using Markdown. Provide citations where applicable."
    private var userPrompt: String = ""
    private var roleContext: String = "Role: STUDENT"
    private var domainContext: Map<String, String> = emptyMap()

    fun setSystemPrompt(prompt: String) = apply {
        this.systemPrompt = prompt
    }

    fun setDeveloperPrompt(prompt: String) = apply {
        this.developerPrompt = prompt
    }

    fun setUserPrompt(rawPrompt: String) = apply {
        this.userPrompt = DataMasker.maskConfidentialData(rawPrompt)
    }

    fun setUserRole(role: String) = apply {
        this.roleContext = "Role: ${role.uppercase()}"
    }

    fun addContext(key: String, value: String) = apply {
        val updated = this.domainContext.toMutableMap()
        updated[key] = value
        this.domainContext = updated
    }

    fun applyCategoryTemplate(category: AiCategory) = apply {
        when (category) {
            AiCategory.ADMISSION -> {
                this.systemPrompt = "You are the Smart University Admission AI Officer."
                this.developerPrompt = "Provide precise admission guidance, requirements, deadlines, and fee structures."
            }
            AiCategory.FEE_PAYMENT -> {
                this.systemPrompt = "You are the Financial Accounts AI Specialist."
                this.developerPrompt = "Assist with payment instructions, invoice breakdowns, and due date reminders."
            }
            AiCategory.ATTENDANCE -> {
                this.systemPrompt = "You are the Academic Attendance Advisor."
                this.developerPrompt = "Analyze attendance ratios, highlight shortages below 75%, and recommend leave filing procedures."
            }
            AiCategory.ASSIGNMENTS -> {
                this.systemPrompt = "You are the Smart Learning Assistant for Course Assignments."
                this.developerPrompt = "Prioritize upcoming deadlines, summarize requirements, and point to relevant library resources."
            }
            AiCategory.RESULTS -> {
                this.systemPrompt = "You are the Academic Performance Analyst."
                this.developerPrompt = "Provide CGPA interpretations, subject breakdown, and grade improvement tips."
            }
            else -> {
                this.systemPrompt = "You are the Smart Campus Enterprise AI Assistant."
            }
        }
    }

    fun buildFinalPrompt(): String {
        val contextStr = if (domainContext.isNotEmpty()) {
            "\n[CAMPUS CONTEXT]:\n" + domainContext.entries.joinToString("\n") { "- ${it.key}: ${it.value}" }
        } else ""

        return """
            [SYSTEM INSTRUCTION]
            $systemPrompt

            [DEVELOPER INSTRUCTION]
            $developerPrompt

            [USER CONTEXT]
            $roleContext
            $contextStr

            [USER QUERY]
            $userPrompt
        """.trimIndent()
    }

    companion object {
        fun buildQuickPrompt(
            category: AiCategory,
            userRole: String,
            rawQuery: String,
            extraContext: Map<String, String> = emptyMap()
        ): String {
            val builder = PromptBuilder()
                .setUserRole(userRole)
                .applyCategoryTemplate(category)
                .setUserPrompt(rawQuery)

            extraContext.forEach { (k, v) -> builder.addContext(k, v) }
            return builder.buildFinalPrompt()
        }
    }
}
