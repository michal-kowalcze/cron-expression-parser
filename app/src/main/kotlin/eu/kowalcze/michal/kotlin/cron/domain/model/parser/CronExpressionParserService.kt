package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.*

class CronExpressionParserService {
    // order parsers from the most specific to the least specific
    private val parsers = listOf(
        AnyValueParser,
        RangeOfValuesParser,
        AnyWithStepParser,
        SingleValueParser,
    )

    fun parse(line: CronExpressionLine): CronExpression {

        val match = CRON_EXPRESSION_FIELDS.matchEntire(line.value)
            ?: throw CronExpressionNotMatched(line)

        // the first group value is at index 1 - and keep 1-based index to make it more human-readable in exception messages
        val fields = match.groupValues
        return CronExpression(
            minute = parseFieldPattern(fields, 1, Minute.RANGE),
            hour = parseFieldPattern(fields, 2, Hour.RANGE),
            dayOfMonth = parseFieldPattern(fields, 3, DayOfMonth.RANGE),
            month = parseFieldPattern(fields, 4, Month.RANGE),
            dayOfWeek = parseFieldPattern(fields, 5, DayOfWeek.RANGE),
            command = Command(fields[6]),
        )
    }

    private fun <TYPE : CalendarField> parseFieldPattern(
        fields: List<String>,
        fieldIndex: Int,
        limit: IntRange
    ): CalendarFieldPattern<TYPE> =
        fields[fieldIndex].split(POSSIBLE_VALUES_DELIMITER)
            .map { parseExceptComma<TYPE>(it, fieldIndex, limit) }
            .let {
                PossibleValuesFieldPattern.optionalWrapWithPossibleValues(it)
            }


    private fun <TYPE : CalendarField> parseExceptComma(
        pattern: String,
        fieldIndex: Int,
        limit: IntRange
    ): CalendarFieldPattern<TYPE> =
        parsers.mapNotNull { it.tryParse<TYPE>(pattern, fieldIndex, limit) }
            .firstOrNull() ?: throw FieldPatternNotMatched(pattern, fieldIndex)
}

private const val POSSIBLE_VALUES_DELIMITER = ","
private val CRON_EXPRESSION_FIELDS = Regex("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.+)")

class FieldPatternNotMatched(pattern: String, fieldIndex: Int) :
    IllegalArgumentException("Provided value: '${pattern}' at index:${fieldIndex} is not recognizable by any known parser")


class CronExpressionNotMatched(line: CronExpressionLine) :
    IllegalArgumentException("Provided input: '${line.value}' does not match a cron expression defined by the regex: $CRON_EXPRESSION_FIELDS")