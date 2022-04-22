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
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.Year
import eu.kowalcze.michal.kotlin.cron.utils.logger
import java.util.*


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
        val commandOrYear = fields[COMMAND_FIELD_INDEX]
        val commandAndYear: MatchResult? =COMMAND_OR_YEAR_PATTERN.matchEntire(commandOrYear)
        return CronExpression(
            minute = parseFieldPattern(fields, MINUTE_FIELD_INDEX, Minute.RANGE),
            hour = parseFieldPattern(fields, HOUR_FIELD_INDEX, Hour.RANGE),
            dayOfMonth = parseFieldPattern(fields, DAY_OF_MONTH_FIELD_INDEX, DayOfMonth.RANGE),
            month = parseFieldPattern(fields, MONTH_FIELD_INDEX, Month.RANGE, ::convertMonth),
            dayOfWeek = parseFieldPattern(fields, DAY_OF_WEEK_FIELD_INDEX, DayOfWeek.RANGE),
            year = commandAndYear?.let {
                parseFieldPattern(it.groupValues, 1, Year.RANGE)
            },
            command = if ( commandAndYear!=null) Command(commandAndYear.groupValues[3]) else Command(commandOrYear),
        )
    }

    private val monthNames = mapOf(
        "JAN" to 1,
        "FEB" to 2,
        "MAR" to 3,
        "APR" to 4,
        "MAY" to 5,
        "JUN" to 6,
        "JUL" to 7,
        "AUG" to 8,
        "SEP" to 9,
        "OCT" to 10,
        "NOV" to 11,
        "DEC" to 12,
    )

    private fun convertMonth(value: String): Int {
        logger.debug("converting month: {}",value)
        return monthNames.get(value.uppercase(Locale.getDefault())) ?: value.toInt()
    }

    private fun <TYPE : CalendarField> parseFieldPattern(
        fields: List<String>,
        fieldIndex: Int,
        limit: IntRange,
        conversion: String.() -> Int = { toInt() },
    ): CalendarFieldPattern<TYPE> =
        fields[fieldIndex].split(POSSIBLE_VALUES_DELIMITER)
            .map { parseExceptComma<TYPE>(it, fieldIndex, limit, conversion) }
            .let {
                PossibleValuesFieldPattern.optionalWrapWithPossibleValues(it)
            }


    private fun <TYPE : CalendarField> parseExceptComma(
        pattern: String,
        fieldIndex: Int,
        limit: IntRange,
        conversion: String.() -> Int,
    ): CalendarFieldPattern<TYPE> =
        parsers.mapNotNull { it.tryParse<TYPE>(pattern, fieldIndex, limit, conversion) }
            .firstOrNull() ?: throw FieldPatternNotMatched(pattern, fieldIndex)


    companion object {
        private val logger by logger()
    }
}

private const val POSSIBLE_VALUES_DELIMITER = ","
private val CRON_EXPRESSION_FIELDS = Regex("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.+)")
private const val MINUTE_FIELD_INDEX = 1
private const val HOUR_FIELD_INDEX = 2
private const val DAY_OF_MONTH_FIELD_INDEX = 3
private const val MONTH_FIELD_INDEX = 4
private const val DAY_OF_WEEK_FIELD_INDEX = 5
private const val COMMAND_FIELD_INDEX = 6
private val COMMAND_OR_YEAR_PATTERN = Regex("([0-9]{4}(-[0-9]{4})?)\\s+(.+)")

class FieldPatternNotMatched(pattern: String, fieldIndex: Int) :
    IllegalArgumentException(
        "Provided value: '${pattern}' at index:${fieldIndex} is not recognizable by any known parser"
    )


class CronExpressionNotMatched(line: CronExpressionLine) :
    IllegalArgumentException(
        "Provided input: '${line.value}' does not match a cron expression defined by the regex: $CRON_EXPRESSION_FIELDS"
    )