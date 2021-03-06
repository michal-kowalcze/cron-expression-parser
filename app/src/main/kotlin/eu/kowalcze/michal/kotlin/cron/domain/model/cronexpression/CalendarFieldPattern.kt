package eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression

sealed interface CalendarFieldPattern<TYPE : CalendarField> {
    fun isMatched(calendarField: CalendarField): Boolean
}

class AnyValueFieldPattern<TYPE : CalendarField> : CalendarFieldPattern<TYPE> {
    override fun isMatched(calendarField: CalendarField) = true
}

class SingleValueFieldPattern<TYPE : CalendarField>(private val value: Int) : CalendarFieldPattern<TYPE> {
    override fun isMatched(calendarField: CalendarField): Boolean = this.value == calendarField.value
}

class RangeOfValuesFieldPattern<TYPE : CalendarField>(private val range: IntProgression) : CalendarFieldPattern<TYPE> {
    override fun isMatched(calendarField: CalendarField): Boolean = this.range.contains(calendarField.value)
}

class PossibleValuesFieldPattern<TYPE : CalendarField>(private val patterns: List<CalendarFieldPattern<TYPE>>) :
    CalendarFieldPattern<TYPE> {

    init {
        if (patterns.isEmpty()) throw NoPatternsProvidedException()
    }

    override fun isMatched(calendarField: CalendarField): Boolean =
        patterns.any { it.isMatched(calendarField) }

    companion object {
        fun <TYPE : CalendarField> optionalWrapWithPossibleValues(patterns: List<CalendarFieldPattern<TYPE>>) =
            if (patterns.size == 1) {
                patterns[0]
            } else {
                PossibleValuesFieldPattern(patterns)
            }
    }
}

class NoPatternsProvidedException :
    IllegalArgumentException("PossibleValuesFieldPattern requires at least one pattern")
