package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.Command
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CronExpression
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.PossibleValuesFieldPattern

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

    private fun parseFieldPattern(pattern: String): CalendarFieldPattern {
        val parsedFields = pattern.split(",")
            .map { parseExceptComma(it) }

        return if (parsedFields.size == 1) {
            parsedFields[0]
        } else {
            PossibleValuesFieldPattern(parsedFields)
        }
    }

    private fun parseExceptComma(pattern: String): CalendarFieldPattern =
        parsers.mapNotNull { it.tryParse(pattern) }
            .firstOrNull() ?: throw FieldPatternNotMatched(pattern)
}

private val CRON_EXPRESSION_FIELDS = Regex("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.+)")

class FieldPatternNotMatched(pattern: String) :
    IllegalArgumentException("Provided value: '${pattern}' is not recognizable by any known parser")


class CronExpressionNotMatched(line: CronExpressionLine) :
    IllegalArgumentException("Provided input: '${line.value}' does not match a cron expression defined by the regex: $CRON_EXPRESSION_FIELDS")