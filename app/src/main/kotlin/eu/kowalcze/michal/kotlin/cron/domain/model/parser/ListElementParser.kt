package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.*

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
            val stepValue = matchResult.groupValues[4]
            val step = if (stepValue.isNotBlank()) stepValue.toInt() else 1
            if (step < 1) throw NonPositiveRangeStepException(value, fieldIndex, step)

            val range = IntProgression.fromClosedRange(
                rangeStart = matchResult.groupValues[1].toInt(),
                rangeEnd = matchResult.groupValues[2].toInt(),
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
}

object AnyWithStepParser : ListElementParser {
    override fun <TYPE : CalendarField> tryParse(
        value: String,
        fieldIndex: Int,
        limit: IntRange
    ) =
        ANY_WITH_STEP_REGEX.matchEntire(value)?.let { matchResult ->
            val step = matchResult.groupValues[1].toInt()

            val range = IntProgression.fromClosedRange(
                rangeStart = limit.first,
                rangeEnd = limit.last,
                step = step,
            )

            RangeOfValuesFieldPattern<TYPE>(range)
        }

    private val ANY_WITH_STEP_REGEX = Regex("\\*/(\\d+)")
}

class EmptyRangeException(value: String, fieldIndex: Int) :
    IllegalArgumentException("Provided value: '${value}' at index:${fieldIndex} defines an empty range")

class RangeOutOfLimitException(value: String, fieldIndex: Int, limit: IntRange) :
    IllegalArgumentException("Provided value: '${value}' at index:${fieldIndex} is not within limit: $limit")


class NonPositiveRangeStepException(value: String, fieldIndex: Int, step: Int) :
    IllegalArgumentException("Provided value: '${value}' at index:${fieldIndex} defines a non-positive step: $step")