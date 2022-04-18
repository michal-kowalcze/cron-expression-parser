package eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression

data class CronExpression constructor(
    val minute: CalendarFieldPattern<Minute>,
    val hour: CalendarFieldPattern<Hour>,
    val dayOfMonth: CalendarFieldPattern<DayOfMonth>,
    val month: CalendarFieldPattern<Month>,
    val dayOfWeek: CalendarFieldPattern<DayOfWeek>,
    val command: Command,
)

data class Command(
    val value: String,
)