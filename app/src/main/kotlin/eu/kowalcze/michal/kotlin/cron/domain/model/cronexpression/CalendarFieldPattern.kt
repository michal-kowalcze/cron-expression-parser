package eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression

sealed interface CalendarFieldPattern {
    fun isMatched(calendarField: CalendarField): Boolean
}

object AnyValueFieldPattern : CalendarFieldPattern {
    override fun isMatched(calendarField: CalendarField) = true
}

class SingleNumberFieldPattern(private val value: Int) : CalendarFieldPattern {
    override fun isMatched(calendarField: CalendarField): Boolean = this.value == calendarField.value
}

class PossibleValuesFieldPattern(private val patterns: List<CalendarFieldPattern>) : CalendarFieldPattern {

    init {
        if (patterns.isEmpty()) throw NoPatternsProvidedException()
    }

    override fun isMatched(calendarField: CalendarField): Boolean =
        patterns.any { it.isMatched(calendarField) }
}


class NoPatternsProvidedException :
    IllegalArgumentException("PossibleValuesFieldPattern requires at least one pattern")