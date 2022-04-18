package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.AnyValueFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarField
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.SingleNumberFieldPattern

sealed interface ListElementParser {
    fun <TYPE : CalendarField> tryParse(value: String, fieldIndex: Int, range: IntRange): CalendarFieldPattern<TYPE>?
}

class ValueOutsideOfLimitException(value: String, fieldIndex: Int, range: IntRange) :
    IllegalArgumentException("Provided value: '${value}' at index:${fieldIndex} is not within limit: $range")

object SingleNumberParser : ListElementParser {

    override fun <TYPE : CalendarField> tryParse(value: String, fieldIndex: Int, range: IntRange) =
        try {
            val parsedValue = value.toInt()
            if (!range.contains(parsedValue)) {
                throw ValueOutsideOfLimitException(value, fieldIndex, range)
            }
            SingleNumberFieldPattern<TYPE>(parsedValue)
        } catch (exc: NumberFormatException) {
            null
        }
}

object AnyValueParser : ListElementParser {
    override fun <TYPE : CalendarField> tryParse(value: String, fieldIndex: Int, range: IntRange) =
        if (value == ALL_VALUES_CHAR) {
            AnyValueFieldPattern<TYPE>()
        } else {
            null
        }

    private const val ALL_VALUES_CHAR = "*"

}