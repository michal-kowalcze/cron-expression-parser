package eu.kowalcze.michal.kotlin.cron.utils

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.*
import kotlin.random.Random

fun randomCalendarField(customization: CalendarFieldConfiguration.() -> Unit): CalendarField {
    val configuration = CalendarFieldConfiguration().apply(customization)
    val fieldType = randomFromInclusiveRange(0, 4)
    return when (fieldType) {
        0 -> randomMinute(configuration.value)
        1 -> randomHour(configuration.value)
        2 -> randomDayOfMonth(configuration.value)
        3 -> randomMonth(configuration.value)
        4 -> randomDayOfWeek(configuration.value)
        else -> throw IllegalArgumentException("Field type $fieldType not handled")
    }
}


fun randomMinute(value: Int? = null) = Minute(value ?: randomFromInclusiveRange(0, 59))
fun randomHour(value: Int? = null) = Hour(value ?: randomFromInclusiveRange(0, 23))
fun randomDayOfMonth(value: Int? = null) = DayOfMonth(value ?: randomFromInclusiveRange(1, 31))
fun randomMonth(value: Int? = null) = Month(value ?: randomFromInclusiveRange(1, 12))
fun randomDayOfWeek(value: Int? = null) = DayOfWeek(value ?: randomFromInclusiveRange(1, 7))

fun randomValueForAnyField() = randomFromInclusiveRange(1, 7)
private fun randomFromInclusiveRange(from: Int, till: Int) = Random.nextInt(from, till + 1)

class CalendarFieldConfiguration {
    var value: Int? = null
}