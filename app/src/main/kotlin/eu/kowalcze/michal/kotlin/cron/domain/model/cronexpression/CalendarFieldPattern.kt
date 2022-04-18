package eu.kowalcze.michal.kotlin.cron.domain.model

interface CalendarFieldPattern {
    fun isMatched(calendarField: CalendarField): Boolean
}

class AnyValue : CalendarFieldPattern {
    override fun isMatched(calendarField: CalendarField) = true
}

class SingleNumberFieldPattern(private val value: Int) : CalendarFieldPattern {
    override fun isMatched(calendarField: CalendarField): Boolean = this.value == calendarField.value
}

class PossibleValues(private val patterns: List<CalendarFieldPattern>) : CalendarFieldPattern {
    override fun isMatched(calendarField: CalendarField): Boolean =
        patterns.any { it.isMatched(calendarField) }
}