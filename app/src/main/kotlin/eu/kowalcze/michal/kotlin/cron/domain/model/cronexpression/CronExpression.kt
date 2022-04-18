package eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression

data class CronExpression constructor(
    val minute: CalendarFieldPattern,
    val hour: CalendarFieldPattern,
    val dayOfMonth: CalendarFieldPattern,
    val month: CalendarFieldPattern,
    val dayOfWeek: CalendarFieldPattern,
    val command: Command,
)

data class Command(
    val value:String,
)