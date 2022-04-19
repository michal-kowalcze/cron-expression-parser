package eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression

data class CronExpressionSummary(
    val minute: List<Minute>,
    val hour: List<Hour>,
    val dayOfMonth: List<DayOfMonth>,
    val month: List<Month>,
    val dayOfWeek: List<DayOfWeek>,
    val command: Command,
)