package eu.kowalcze.michal.kotlin.cron.domain.model

abstract class CalendarField(
    val value: Int
)

class Minute(value: Int) : CalendarField(value)
class Hour(value: Int) : CalendarField(value)
class DayOfMonth(value: Int) : CalendarField(value)
class Month(value: Int) : CalendarField(value)
class DayOfWeek(value: Int) : CalendarField(value)