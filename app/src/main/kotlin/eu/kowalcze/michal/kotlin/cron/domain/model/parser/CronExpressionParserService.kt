package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.*

class CronExpressionParserService {
    // order parsers from the most specific to the least specific
    //TODO support other pattern expressions
    private val parsers = listOf(
        AnyValueParser(),
        SingleNumberParser(),
    )

    //TODO add range limits
    fun parse(line: CronExpressionLine): CronExpression {

        val match = CRON_EXPRESSION_FIELDS.matchEntire(line.value)
            ?: throw CronExpressionNotMatched(line)

        return CronExpression(
            minute = parseFieldPattern(match.groupValues[1]),
            hour = parseFieldPattern(match.groupValues[2]),
            dayOfMonth = parseFieldPattern(match.groupValues[3]),
            month = parseFieldPattern(match.groupValues[4]),
            dayOfWeek = parseFieldPattern(match.groupValues[5]),
            command = Command(match.groupValues[6]),
        )
    }

    private fun <TYPE : CalendarField> parseFieldPattern(pattern: String): CalendarFieldPattern<TYPE> {
        val parsedFields = pattern.split(",")
            .map { parseExceptComma<TYPE>(it) }

        return if (parsedFields.size == 1) {
            parsedFields[0]
        } else {
            PossibleValuesFieldPattern(parsedFields)
        }
    }

    private fun <TYPE : CalendarField> parseExceptComma(pattern: String): CalendarFieldPattern<TYPE> =
        parsers.mapNotNull { it.tryParse(pattern) }
            .firstOrNull() ?: throw FieldPatternNotMatched(pattern)
}

private val CRON_EXPRESSION_FIELDS = Regex("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.+)")

class FieldPatternNotMatched(pattern: String) :
    IllegalArgumentException("Provided value: '${pattern}' is not recognizable by any known parser")


class CronExpressionNotMatched(line: CronExpressionLine) :
    IllegalArgumentException("Provided input: '${line.value}' does not match a cron expression defined by the regex: $CRON_EXPRESSION_FIELDS")