package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.AnyValueFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarField
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.RangeOfValuesFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.SingleValueFieldPattern

sealed interface ListElementParser {
    fun <TYPE : CalendarField> tryParse(value: String, fieldIndex: Int, limit: IntRange): CalendarFieldPattern<TYPE>?
}

object SingleValueParser : ListElementParser {

    override fun <TYPE : CalendarField> tryParse(value: String, fieldIndex: Int, limit: IntRange) =
        try {
            val parsedValue = value.toInt()
            if (!limit.contains(parsedValue)) {
                throw SingleValueOutsideOfLimitException(value, fieldIndex, limit)
            }
            SingleValueFieldPattern<TYPE>(parsedValue)
        } catch (exc: NumberFormatException) {
            null
        }
}

class SingleValueOutsideOfLimitException(value: String, fieldIndex: Int, range: IntRange) :
    IllegalArgumentException("Provided value: '${value}' at index:${fieldIndex} is not within limit: $range")

object AnyValueParser : ListElementParser {
    override fun <TYPE : CalendarField> tryParse(value: String, fieldIndex: Int, limit: IntRange) =
        if (value == ALL_VALUES_CHAR) {
            AnyValueFieldPattern<TYPE>()
        } else {
            null
        }

    private const val ALL_VALUES_CHAR = "*"
}

object RangeOfValuesParser : ListElementParser {
    override fun <TYPE : CalendarField> tryParse(
        value: String,
        fieldIndex: Int,
        limit: IntRange
    ) =
        RANGE_REGEX.matchEntire(value)?.let { matchResult ->
            val stepValue = matchResult.groupValues[STEP_INDEX]
            val step = if (stepValue.isNotBlank()) stepValue.toInt() else 1
            if (step < 1) throw NonPositiveRangeStepException(value, fieldIndex, step)

            val range = IntProgression.fromClosedRange(
                rangeStart = matchResult.groupValues[RANGE_START_INDEX].toInt(),
                rangeEnd = matchResult.groupValues[RANGE_END_INDEX].toInt(),
                step = step,
            )

            if (range.isEmpty()) {
                throw EmptyRangeException(value, fieldIndex)
            }
            if (!limit.contains(range.first) || !limit.contains(range.last)) {
                throw RangeOutOfLimitException(value, fieldIndex, limit)
            }
            RangeOfValuesFieldPattern<TYPE>(range)
        }

    private val RANGE_REGEX = Regex("(\\d+)-(\\d+)(/(\\d+))?")
    private const val RANGE_START_INDEX=1
    private const val RANGE_END_INDEX=2
    private const val STEP_INDEX=4
}

object AnyWithStepParser : ListElementParser {
    override fun <TYPE : CalendarField> tryParse(
        value: String,
        fieldIndex: Int,
        limit: IntRange
    ) =
        ANY_WITH_STEP_REGEX.matchEntire(value)?.let { matchResult ->
            val step = matchResult.groupValues[STEP_GROUP_INDEX].toInt()

            val range = IntProgression.fromClosedRange(
                rangeStart = limit.first,
                rangeEnd = limit.last,
                step = step,
            )

            RangeOfValuesFieldPattern<TYPE>(range)
        }

    private val ANY_WITH_STEP_REGEX = Regex("\\*/(\\d+)")
    private const val STEP_GROUP_INDEX=1
}

class EmptyRangeException(value: String, fieldIndex: Int) :
    IllegalArgumentException("Provided value: '${value}' at index:${fieldIndex} defines an empty range")

class RangeOutOfLimitException(value: String, fieldIndex: Int, limit: IntRange) :
    IllegalArgumentException("Provided value: '${value}' at index:${fieldIndex} is not within limit: $limit")


class NonPositiveRangeStepException(value: String, fieldIndex: Int, step: Int) :
    IllegalArgumentException("Provided value: '${value}' at index:${fieldIndex} defines a non-positive step: $step")
