package eu.kowalcze.michal.kotlin.cron.domain.usecase


data class ParserInput(
    val minute: String,
    val hour: String,
    val dayOfMonth: String,
    val month: String,
    val dayOfWeek: String,
    val command: String,
)
