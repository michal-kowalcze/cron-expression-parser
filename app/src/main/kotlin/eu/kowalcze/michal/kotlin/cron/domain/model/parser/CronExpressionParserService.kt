package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.Command
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CronExpression
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.PossibleValuesFieldPattern
data class ParserInput(
    val minute: String,
    val hour: String,
    val dayOfMonth: String,
    val month: String,
    val dayOfWeek: String,
    val command: String,
)

class CronExpressionParserService {
    // order parsers from the most specific to the least specific
    private val parsers = listOf(
        AnyValueParser(),
        SingleNumberParser(),
    )

    //TODO add range limits
    fun parse(parserInput: ParserInput) = CronExpression(
        minute = parseFieldPattern(parserInput.minute),
        hour = parseFieldPattern(parserInput.hour),
        dayOfMonth = parseFieldPattern(parserInput.dayOfMonth),
        month = parseFieldPattern(parserInput.month),
        dayOfWeek = parseFieldPattern(parserInput.dayOfWeek),
        command = Command(parserInput.command),
    )

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

class FieldPatternNotMatched(pattern: String) :
    IllegalArgumentException("Provided value: '${pattern}' is not recognizable by any of known parsers")

