package eu.kowalcze.michal.kotlin.cron.domain.model.parser

data class CronExpressionLine private constructor(
    val value: String,
) {
    companion object {
        fun from(input: String) = CronExpressionLine(input.trim())
    }
}