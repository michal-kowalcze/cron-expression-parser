package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarField
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.Command
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CronExpression
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.DayOfMonth
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.DayOfWeek
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.Hour
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.Minute
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.Month
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.PossibleValuesFieldPattern


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

        // the first group value is at index 1 - and keep 1-based index
        // to make it more human-readable in exception messages
        val fields = match.groupValues
        return CronExpression(
            minute = parseFieldPattern(fields, MINUTE_FIELD_INDEX, Minute.RANGE),
            hour = parseFieldPattern(fields, HOUR_FIELD_INDEX, Hour.RANGE),
            dayOfMonth = parseFieldPattern(fields, DAY_OF_MONTH_FIELD_INDEX, DayOfMonth.RANGE),
            month = parseFieldPattern(fields, MONTH_FIELD_INDEX, Month.RANGE),
            dayOfWeek = parseFieldPattern(fields, DAY_OF_WEEK_FIELD_INDEX, DayOfWeek.RANGE),
            command = Command(fields[COMMAND_FIELD_INDEX]),
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
private const val MINUTE_FIELD_INDEX = 1
private const val HOUR_FIELD_INDEX = 2
private const val DAY_OF_MONTH_FIELD_INDEX = 3
private const val MONTH_FIELD_INDEX = 4
private const val DAY_OF_WEEK_FIELD_INDEX = 5
private const val COMMAND_FIELD_INDEX = 6

class FieldPatternNotMatched(pattern: String, fieldIndex: Int) :
    IllegalArgumentException(
        "Provided value: '${pattern}' at index:${fieldIndex} is not recognizable by any known parser"
    )


class CronExpressionNotMatched(line: CronExpressionLine) :
    IllegalArgumentException(
        "Provided input: '${line.value}' does not match a cron expression defined by the regex: $CRON_EXPRESSION_FIELDS"
    )
