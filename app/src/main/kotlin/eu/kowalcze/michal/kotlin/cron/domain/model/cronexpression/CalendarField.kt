package eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression

sealed class CalendarField(
    val value: Int,
)

class Minute(value: Int) : CalendarField(value) {
    companion object {
        val RANGE = IntRange(0, 59)
        fun allValues() = RANGE.map { Minute(it) }
    }
}

class Hour(value: Int) : CalendarField(value) {
    companion object {
        val RANGE = IntRange(0, 23)
        fun allValues() = RANGE.map { Hour(it) }
    }
}

class DayOfMonth(value: Int) : CalendarField(value) {
    companion object {
        val RANGE = IntRange(1, 31)
        fun allValues() = RANGE.map { DayOfMonth(it) }
    }
}

class Month(value: Int) : CalendarField(value) {
    companion object {
        val RANGE = IntRange(1, 12)
        fun allValues() = RANGE.map { Month(it) }
    }
}

class DayOfWeek(value: Int) : CalendarField(value) {
    companion object {
        val RANGE = IntRange(1, 7)
        fun allValues() = RANGE.map { DayOfWeek(it) }
    }
}
